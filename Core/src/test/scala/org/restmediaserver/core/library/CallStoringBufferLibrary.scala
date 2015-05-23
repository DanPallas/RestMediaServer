package org.restmediaserver.core.library

import java.io.File

import org.restmediaserver.core.files.mediafiles.MediaFile

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

/** Useful for testing how a BufferLibrary is called.
  * @author Dan Pallas
  * @since v1.0 on 5/21/15.
  */
class CallStoringBufferLibrary()(implicit ec: ExecutionContext) extends BufferLibrary {
  val getMediaLibraryCalls = mutable.Buffer[File]()
  val putMediaFileCalls = mutable.Buffer[MediaFile]()
  val removeMediaFileCalls = mutable.Buffer[String]()

  override def getLibraryFolder(path: File): Future[Option[LibraryFolder]] = {
    getMediaLibraryCalls.synchronized{
      getMediaLibraryCalls += path
    }
    super.getLibraryFolder(path)
  }

  override def putMediaFile(mediaFile: MediaFile): Future[Boolean] = {
    putMediaFileCalls.synchronized{
      putMediaFileCalls += mediaFile
    }
    super.putMediaFile(mediaFile)
  }

  override def removeMediaFile(path: String): Future[Boolean] = {
    removeMediaFileCalls.synchronized{
      removeMediaFileCalls += path
    }
    super.removeMediaFile(path)
  }
}
object CallStoringBufferLibrary {
  def apply()(implicit ec: ExecutionContext) = {
    new CallStoringBufferLibrary()
  }
}
