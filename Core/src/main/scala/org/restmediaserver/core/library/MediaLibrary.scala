package org.restmediaserver.core.library

import java.io.File

import org.restmediaserver.core.files.mediafiles.MediaFile

import scala.concurrent.Future

/** Persistence service for media file library
  * @author Dan Pallas
  * @since v1.0 on 4/12/15.
 */
abstract class MediaLibrary {
  def getLibraryFolder(path: File): Future[Option[LibraryFolder]]
  def removeMediaFile(path: String): Future[Boolean]
  def putMediaFile(mediaFile: MediaFile): Future[Boolean]
  def getSubDirs(parent: File): Future[Set[String]]
  def removeLibraryFolder(path: String): Future[Int]
}
object MediaLibrary {
}
