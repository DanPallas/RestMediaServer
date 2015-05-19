package org.restmediaserver.core.testfixtures

import java.io.{File, IOException}

import org.restmediaserver.core.files.mediafiles.MediaFile
import org.restmediaserver.core.library.{LibraryFolder, MediaLibrary}

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

/** Mock MediaLibrary base which accepts all add and remove requests and puts them in their associated buffers
  * and returns None for getLibraryFolder */
class MockMediaLibrary(implicit val ec: ExecutionContext) extends MediaLibrary {
  var addBuffer = mutable.Buffer[MediaFile]()
  var removeBuffer = mutable.Buffer[String]()

  override def putMediaFile(mediaFile: MediaFile): Future[Boolean] = {
    if (shouldAdd(mediaFile)) {
      addBuffer += mediaFile
      Future(true)
    } else {
      Future(false)
    }
  }

  override def removeMediaFile(path: String): Future[Boolean] = {
    if(shouldRemove(path)){
      removeBuffer += path
      Future(true)
    } else {
      Future{false}
    }
  }

  /** configure behavior determining the result of putMediaFile.
    * @return true if file should be added. False if file should not be added. throw exception if putMediaFile
    * should throw exception */
  @throws(classOf[IOException])
  protected def shouldAdd(mediaFile: MediaFile): Boolean = true

  /** configure behavior determining the result of removeMediaFile.
    * @return true if file should be removed. False if file should not be removed. throw exception if remove MediaFile
    * should throw exception */
  @throws(classOf[IOException])
  protected def shouldRemove(path: String): Boolean = true

  override def getLibraryFolder(path: File): Future[Option[LibraryFolder]] = Future{None}
}
object MockMediaLibrary {
  def apply()(implicit ec: ExecutionContext) = new MockMediaLibrary()(ec)
}