package org.restmediaserver.core.mediascanner

import java.io.File

import akka.actor.{Actor, ActorRef, Props}
import com.typesafe.scalalogging.LazyLogging
import org.restmediaserver.core.ActorMessage
import org.restmediaserver.core.files.mediafiles.MediaFile
import org.restmediaserver.core.library.{LibraryFile, LibraryFolder, MediaLibrary}
import org.restmediaserver.core.mediascanner.FolderUpdaterDelegate._

import scala.collection.mutable


/** Adds, deletes or updates a single folder to the library
 * Created by Dan Pallas on 4/29/15.
 */
protected class FolderUpdaterDelegate(private var folder: File,
                                      private val library: ActorRef,
                                      private val updaterProps: Props)
  extends Actor with LazyLogging{

  private val toUpdate = mutable.Set[File]()
  private val toRemove = mutable.Set[String]()
  private lazy val updater = context.actorOf(updaterProps, UpdaterChildName)
  private var wasFailure = false
  if(!folder.isDirectory) {
    context.parent ! Failure(folder)
  } else {
    library ! MediaLibrary.GetLibraryFolderMsg(folder)
  }

  private def handleLibraryFolder(libraryFolder: LibraryFolder): Unit = {
    def isNewer(currentFile: File): Boolean = {
        val maybeLibraryFile = libraryFolder.children.find(_.path == currentFile.getPath)
        maybeLibraryFile match {
          case Some(libraryFile) => libraryFile.modTime < currentFile.lastModified()
          case None => true
      }
    }

    def noLongerExists(libraryFile: LibraryFile, currentFiles: Seq[File]): Boolean = {
      !currentFiles.exists(_.getPath == libraryFile.path)
    }

    val mediaFiles: Seq[File] = folder.listFiles().filter(MediaFile.getFileType(_).isDefined).toSeq
    toUpdate ++= mediaFiles.filter(isNewer)
    toRemove ++= libraryFolder.children filter (noLongerExists(_, mediaFiles)) map (_.path)
    if(toUpdate.nonEmpty) toUpdate.foreach(updater ! _)
    toRemove.foreach(library ! MediaLibrary.RemoveSong(_))
  }

  private def handleSuccess(file: File): Unit = {
    toUpdate -= file
    maybeFinish()
  }

  private def maybeFinish(): Unit ={
    if(toUpdate.isEmpty && toRemove.isEmpty){
      if (wasFailure) context.parent ! Failure(folder)
      else context.parent ! Success(folder)
    }
  }

  private def handleFailure(file: File): Unit = {
    wasFailure = true
    toUpdate -= file
    maybeFinish()
  }

  private def handleSuccessfulRemove(path: String): Unit = {
    toRemove -= path
    maybeFinish()
  }

  private def handleRemoveException(path: String, ex: Exception): Unit = {
    //todo possibly retry transient failure
    wasFailure = true
    toRemove -= path
    maybeFinish()
  }

  override def receive: Receive = {
    case libraryFolder: LibraryFolder => handleLibraryFolder(libraryFolder)
    case MediaFileUpdater.Success(f) => handleSuccess(f)
    case MediaFileUpdater.Failed(f) => handleFailure(f)
    case MediaLibrary.SuccessfulRemoveSong(path) => handleSuccessfulRemove(path)
    case MediaLibrary.RemoveSongException(path, ex) => handleRemoveException(path, ex)
  }
}
object FolderUpdaterDelegate {
  def props(folder: File, library: ActorRef, updaterProps: Props): Props = {
    Props(classOf[FolderUpdaterDelegate], folder, library, updaterProps)
  }
  private val UpdaterChildName = "updater"
  case class Success(folder: File) extends ActorMessage
  case class Failure(folder: File) extends ActorMessage
}
