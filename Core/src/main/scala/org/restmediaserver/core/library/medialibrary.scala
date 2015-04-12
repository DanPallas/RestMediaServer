package org.restmediaserver.core.library

import org.restmediaserver.core.files.mediafiles.Song

import scala.reflect.io.File

/** Persistence service for media file library
  * @author Dan Pallas
  * @since v1.0 on 4/12/15.
 */
abstract class MediaLibrary {
  def putSong(song: Song): Unit
  def getSong(path: File): Song
  def getLibraryFolder(path: File): LibraryFolder
  def putFolder(folder: File): Unit
}
