package org.restmediaserver.core.files.mediafiles

import java.io.File

import akka.actor.Actor

/** When sent a File that is the path of an actual MediaFile, the file is read in from the file system and
  * Option[MediaFile] is sent back.
  * @author Dan Pallas
  * @since v1.0 on 4/25/15
 */
class MediaFileReader extends Actor{
  val TestSystem: String = "MetaDataTestSystem"
  override def receive: Receive = {
    case path: File => sender ! MediaFile(path)
  }
}
