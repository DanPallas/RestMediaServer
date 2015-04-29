package org.restmediaserver.core.files.mediafiles

import java.io.{File, IOException}

import akka.actor.Actor
import com.typesafe.scalalogging.LazyLogging

/** When sent a File that is the path of an actual MediaFile, the file is read in from the file system and
  * a MediaFile is sent back. If the file can not be read as a MediaFile then sends back Failed(file)
  * @author Dan Pallas
  * @since v1.0 on 4/25/15
 */
class MediaFileReader extends Actor with LazyLogging{
  import org.restmediaserver.core.files.mediafiles.MediaFileReader.Failed
  val TestSystem: String = "MetaDataTestSystem"
  override def receive: Receive = {
    case path: File =>
      try {
        val result = MediaFile(path)
        result match {
          case Some(song) =>
            sender ! song
          case None => sender ! Failed(path)
        }
      } catch {
        case e: IOException => {
          logger.error(s"Failed to load media file $path", e)
        }
        case e: Throwable => throw e
      }
  }
}
object MediaFileReader{
  /** path could not be read in as MediaFile */
  case class Failed(path: File)
}