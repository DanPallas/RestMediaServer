package org.restmediaserver.core.library

import akka.actor.Actor
import org.restmediaserver.core.files.mediafiles.Song

import scala.reflect.io.File

/** Persistence service for media file library
  * @author Dan Pallas
  * @since v1.0 on 4/12/15.
 */
abstract class MediaLibrary extends Actor {
  case class putSongMsg(song: Song)
  case class getSongMsg(song: Song)
  case class getLibraryFolderMsg(path: File)
  case class putFolderMsg(folder: File)
}
