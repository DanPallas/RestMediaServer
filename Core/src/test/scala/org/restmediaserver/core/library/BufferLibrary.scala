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
        contents.filter(_.parent == path.getPath) toSeq;
      }
      val children = contained.map(f => LibraryFile(f.path, f.modTime))
      if(children.nonEmpty) Some(LibraryFolder(children)) else None
    }
  }

  override def putMediaFile(mediaFile: MediaFile): Future[Boolean] = {
    Future {
      contents.synchronized {
        val existing: Option[MediaFile] = contents.find(_.path == mediaFile.path)
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
        val existing = contents.find(_.path == path)
        existing match {
          case Some(e) =>
            contents -= e
            true
          case None => false
        }
      }
    }
  }

  override def getSubDirs(parent: File): Future[Set[String]] = {
    val pred = MediaFile.isChild(parent.getAbsolutePath)
    Future {
      contents.synchronized{
        contents filter pred map (_.parent) toSet
      }
    }
  }

  override def removeLibraryFolder(path: String): Future[Int] = {
    val pred = MediaFile.isChild(path)
    Future {
      contents.synchronized{
        val matching =  contents filter pred
        contents --= matching
        matching.size
      }
    }
  }
}
object BufferLibrary {
  def apply()(implicit ec: ExecutionContext) = new BufferLibrary()
}
