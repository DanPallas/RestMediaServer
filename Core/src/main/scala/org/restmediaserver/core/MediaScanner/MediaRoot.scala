package org.restmediaserver.core.mediascanner

import java.io.File

import com.typesafe.scalalogging.LazyLogging
import org.restmediaserver.core.files.mediafiles.{AsyncMediaFileService, MediaFile}
import org.restmediaserver.core.library.{LibraryFile, LibraryFolder, MediaLibrary}
import scala.util.matching.Regex

import scala.concurrent.{ExecutionContext, Future}

/** A root media folder who's media contents should be kept in sync with the library.
  * @param path root folder that this class should operate on
  * @param lib library which should be kept in sync with this media root
  * @param ec Execution context to use for Async operations Not in asmf and the library
  * @param fileFilter pred. Files for which fileFilter returns false will not be added to the library and will be
  *                   removed from the library if they already exist
  * @author Dan Pallas
  * @since v1.0 on 4/12/15.
 */
class MediaRoot(private val path: File,
                implicit val lib: MediaLibrary,
                implicit private val asmf: AsyncMediaFileService,
                private val fileFilter: File => Boolean,
                implicit private val ec: ExecutionContext) extends LazyLogging{

  /**
   * Start scan for media files, updating the library for changes since last scan. If watching will also be used
   * it should be started before the scan to make sure that changes to already scanned folders are caught and the
   * the library is updated.
   * @return true if no errors occure during scan, otherwise false
   */
  def startScan(): Future[Int] = {
    def removeDirs(onDisk: IndexedSeq[File], inLib: Set[String]): Future[Int] = {
      /** reduces paths to the top most paths that contain them */
      def topLevelDirs(dirs: IndexedSeq[String]): IndexedSeq[String] = {
        val sorted = dirs.sortBy(_.count(_ == File.separatorChar))
        sorted.foldLeft(Vector[String]()) { (topLevel, dir) =>
          topLevel.exists(tld => new Regex(tld + File.separator + ".*").pattern.matcher(dir).matches()) match {
            case true => topLevel
            case false => topLevel :+ dir
          }
        }
      }
      val existingSet = onDisk.map(_.getAbsolutePath) toSet
      val toRemove = topLevelDirs(inLib diff existingSet toVector)
      val fRemoved = toRemove map lib.removeLibraryFolder
      val removed = Future.sequence(fRemoved)
      removed map (_.sum)
    }

    val dirs = Future(calcDirList())
    val modded = dirs flatMap (d => updateDirs(d))
    val libDirs = lib.getSubDirs(path)
    val removed = libDirs flatMap { lds =>
      dirs flatMap (ds => removeDirs(ds,lds))
    }
    Future.sequence(Seq(modded,removed)) map (_.sum)
  }

  /** get list including this path and all subdirectories under this path */
  private def calcDirList(): Vector[File] = {
    def helper(dir: File): Array[File] ={
      val subDirs = dir.listFiles() filter (_.isDirectory)
      val subDirsRecursive = (for(subDir <- subDirs) yield helper(subDir)).flatten
      dir +: subDirsRecursive
    }
    helper(path).toVector
  }

  private def updateDirs(dirList: IndexedSeq[File]): Future[Int] = {
    /** get a list of Processables for the dir. These processables represent changes in the media files of dir which
      * match fileFilter and are different than what is in the library. This is non recursive and only inspects
      * immediate children of dir. Files which are filtered by fileFilter will be removed from the library if present */
    def getProcessables(dir: File): Future[IndexedSeq[Processable]] = {
      def fetchLibraryFolder(): Future[LibraryFolder] = {
        val fLibraryFolder = lib.getLibraryFolder(dir)
        fLibraryFolder map (optLibrary => optLibrary.getOrElse(LibraryFolder()))
      }
      def getRemovables(lf: LibraryFolder, currentFiles: Seq[File]): IndexedSeq[Removeable] = {
        val noLongerExists = (lFile: LibraryFile) => !currentFiles.exists(_.getPath == lFile.path)
        val toRemove = lf.children filter noLongerExists map (_.path)
        toRemove map (Removeable(_)) toVector
      }
      def getPutables(lf: LibraryFolder, currentFiles: Seq[File]): IndexedSeq[Putable] = {
        logger.debug(s"lf: $lf \n currentFiles $currentFiles")
        val isNewer = (currentFile: File) => {
          val existing = lf.children find (_.path == currentFile.getPath)
          existing match {
            case Some(f) => f.modTime < currentFile.lastModified()
            case None => true
          }
        }
        val putables: Vector[Putable] = currentFiles.filter(isNewer(_)).map(new Putable(_)).toVector
        logger.debug(s"putables: ${putables.toString()}")
        putables
      }
      def getCurrentFiles(dir: File): Seq[File] =
        (path.listFiles() filter (_.isFile) filter (f => MediaFile.getFileType(f).isDefined && fileFilter(f))).toSeq
      for{
        lf <- fetchLibraryFolder()
        currentFiles <- Future(getCurrentFiles(dir))
        removeables <- Future { getRemovables(lf, currentFiles) }
        putables <- Future { getPutables(lf, currentFiles) }
        processables <- Future { removeables ++ putables }
      } yield processables
    }

    val processablesPerChild = dirList map getProcessables
    val result = Future.sequence {
      processablesPerChild map { processableList =>
        processableList flatMap (processable => Future.sequence(processable map (_.process())))
      }
    }
    result map {r =>
      r.flatten.foldLeft(0)((acc: Int, b: Boolean) => acc + (if(b) 1 else 0))
    }
  }


  /** watch for modifications, additions, or deletions occurring to media files in this directory. Any changes
    * found in these mediafiles will be reflected in the Library. This should be called before scanning to make sure
    * that changes that occur durring scanning are reflected in the library. The scan will continue unless there is an
    * exception and until stop is called. **/
  def watch(): Unit = ??? //TODO watch

  /** stop watching folder
   * @return true if stopped without exception. Otherwise false.
   */
  def stop(): Boolean = ??? //TODO stop watching


  /** Objects of this trait can be processed. Processing will update the library by adding, removing, or updating the
    * library. */
  private sealed trait Processable {
    def process(): Future[Boolean]
  }
  private case class Putable(path: File)(implicit val asmf: AsyncMediaFileService, implicit val lib: MediaLibrary)
    extends Processable {
    override def process(): Future[Boolean] = {
      val omf = asmf.read(path)
      omf flatMap {
        case Some(mf) => lib putMediaFile mf
        case None => Future(false)
      }
    }
  }
  private case class Removeable(path: String)(implicit val lib: MediaLibrary) extends Processable {
    override def process(): Future[Boolean] = {
      lib removeMediaFile path
    }
  }
}
object MediaRoot {
  /** @param path root folder that this class should operate on
    * @param lib library which should be kept in sync with this media root
    * @param ec Execution context to use for Async operations Not in asmf and the library
    * @param fileFilter pred. Files for which fileFilter returns false will not be added to the library and will be
    *                   removed from the library if they already exist
    * @return new MediaRoot
    * */
  def apply(path: File, lib: MediaLibrary, asmf: AsyncMediaFileService, fileFilter: File => Boolean)
           (implicit ec: ExecutionContext): MediaRoot = new MediaRoot(path, lib, asmf, fileFilter, ec)

  /** unfiltered MediaRoot
    * @param path root folder that this class should operate on
    * @param lib library which should be kept in sync with this media root
    * @param ec Execution context to use for Async operations Not in asmf and the library
    * @return new MediaRoot
    */
  def apply(path: File, lib: MediaLibrary, asmf: AsyncMediaFileService)
           (implicit ec: ExecutionContext): MediaRoot = new MediaRoot(path, lib, asmf, x => true, ec)
}
