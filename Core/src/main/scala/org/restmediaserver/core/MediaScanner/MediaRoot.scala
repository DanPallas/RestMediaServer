package org.restmediaserver.core.mediascanner

import java.io.File

import org.restmediaserver.core.files.mediafiles.{MediaFile, AsyncMediaFileService}
import org.restmediaserver.core.library.{LibraryFile, LibraryFolder, MediaLibrary}

import scala.concurrent.{Future, ExecutionContext}

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
                implicit private val ec: ExecutionContext,
                implicit private val asmf: AsyncMediaFileService,
                private val fileFilter: File => Boolean) {

  /**
   * Start scan for media files, updating the library for changes since last scan. If watching will also be used
   * it should be started before the scan to make sure that changes to already scanned folders are caught and the
   * the library is updated.
   * @return true if no errors occure during scan, otherwise false
   */
  def startScan(threads: Double): Future[Int] = {
    /** get list including this path and all subdirectories under this path */
    def fetchDirectoryList(): Vector[File] = {
      def helper(dir: File): Array[File] ={
        val subDirs = dir.listFiles() filter (_.isDirectory)
        val subDirsRecursive = (for(subDir <- subDirs) yield helper(subDir)).flatten
        dir +: subDirsRecursive
      }
      helper(path).toVector
    }
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
        val isNewer = (currentFile: File) =>
          lf.children.exists(f => f.path == currentFile.getPath && f.modTime < currentFile.lastModified())
        currentFiles filter isNewer map (Putable(_)) toVector
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

    val dirList = fetchDirectoryList()
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
  def watch(): Unit = ???

  /** stop watching folder
   * @return true if stopped without exception. Otherwise false.
   */
  def stop(): Boolean = ???


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

