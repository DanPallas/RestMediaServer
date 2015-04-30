package org.restmediaserver.core.mediascanner

import java.io.File

import akka.actor.{Props, Actor, ActorRef}
import com.typesafe.scalalogging.LazyLogging
import org.restmediaserver.core.ActorMessage
import org.restmediaserver.core.files.mediafiles.{MediaFile, MediaFileReader, Song}
import org.restmediaserver.core.library.MediaLibrary

/** Actor which receives java.io.File, reads in the metadata from the file system and adds that MediaFile to the library
  * Accepts: java.io.File
  * Responds with:
  *   Success: if file has been put into library or if file is already in library
  *   Failed: if the file was not a valid media file or if adding the file to the library failed for a reason other than
  *     already being in the library
  * @author Dan Pallas
  * @since v1.0 on 4/23/15
  */
class MediaFileUpdater(library: ActorRef, reader: ActorRef) extends Actor with LazyLogging{
  
  override def receive: Receive = {
    case path: File =>
      logger.debug(s"received file $path")
      reader ! path
    case mf: MediaFile =>
      logger.debug(s"received MediaFile ${mf.path}")
      mf match {
        case s: Song =>
          logger.debug("MediaFile matched as song")
          library ! MediaLibrary.PutSongMsg(s)
        case _ =>
          unhandled(mf)
      }
    case MediaFileReader.Failed(path) =>
      logger.error(s"Failed to read $path")
      context.parent ! MediaFileUpdater.Failed(path)
    case MediaLibrary.SuccessfulPut(path) =>
      logger.debug(s"received successful put for file $path")
      context.parent ! MediaFileUpdater.Success(path)
    case MediaLibrary.NotPutOlder(path) => context.parent ! MediaFileUpdater.Success(path)
    case MediaLibrary.PutException(path, ex) =>
      logger.error(s"failed to put $path", ex)
  }
}
object MediaFileUpdater {
  case class Failed(path: File) extends ActorMessage
  case class Success(path: File) extends ActorMessage

  def props(library: ActorRef, reader: ActorRef) = Props(classOf[MediaFileUpdater], library, reader)
}
