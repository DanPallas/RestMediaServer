package org.restmediaserver.core.library

import java.io.File

import com.typesafe.scalalogging.LazyLogging
import org.restmediaserver.core.files.mediafiles.MediaFile

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

/**
 * Buffer based MediaLibrary. Only suitable for tests with small numbers of files
 * @author Dan Pallas
 * @since v1.0 on 5/9/15.
 */
class BufferLibrary()(implicit ec: ExecutionContext) extends MediaLibrary with LazyLogging{
  var contents = mutable.Buffer[MediaFile]()

  override def getLibraryFolder(path: File): Future[Option[LibraryFolder]] = {
    Future {
      val contained = contents.synchronized {
        contents.filter(_.path.getParent == path.getPath) toSeq;
      }
      val children = contained.map(f => LibraryFile(f.path.getPath, f.modTime))
      if(children.nonEmpty) Some(LibraryFolder(children)) else None
    }
  }

  override def putMediaFile(mediaFile: MediaFile): Future[Boolean] = {
    Future {
      contents.synchronized {
        val existing: Option[MediaFile] = contents.find(_.path.getPath == mediaFile.path.getPath)
        existing match {
          case Some(e) =>
            if (e.modTime < mediaFile.modTime) {
              contents -= e
              contents += mediaFile
              true
            } else {
              false
            }
          case None =>
            contents += mediaFile
            true
        }
      }
    }
  }

  override def removeMediaFile(path: String): Future[Boolean] = {
    Future {
      contents.synchronized {
        val existing = contents.find(_.path.getPath == path)
        existing match {
          case Some(e) =>
            contents -= e
            true
          case None => false
        }
      }
    }
  }
}
object BufferLibrary {
  def apply()(implicit ec: ExecutionContext) = new BufferLibrary()
}
