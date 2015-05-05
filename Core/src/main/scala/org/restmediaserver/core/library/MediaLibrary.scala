package org.restmediaserver.core.library

import akka.actor.Actor
import org.restmediaserver.core.ActorMessage
import org.restmediaserver.core.files.mediafiles.Song

import java.io.File

/** Persistence service for media file library
  * @author Dan Pallas
  * @since v1.0 on 4/12/15.
 */
abstract class MediaLibrary extends Actor {

}
object MediaLibrary {
  case class PutSongMsg(song: Song) extends ActorMessage
  case class RemoveSong(path: String) extends ActorMessage
  case class SuccessfulRemoveSong(path: String) extends ActorMessage
  case class RemoveSongException(path: String, ex: Exception) extends ActorMessage
  case class SuccessfulPut(path: File) extends ActorMessage
  case class NotPutOlder(path: File) extends ActorMessage
  case class PutException(path: File, ex: Exception) extends ActorMessage
  case class GetSongMsg(song: Song) extends ActorMessage
  case class GetLibraryFolderMsg(path: File) extends ActorMessage
  case class PutFolderMsg(folder: File) extends ActorMessage
}
