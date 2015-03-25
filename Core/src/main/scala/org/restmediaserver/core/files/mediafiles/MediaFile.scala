package org.restmediaserver.core.files.mediafiles

import java.io.File

import org.restmediaserver.core.files.mediafiles.MediaFile.FileType.FileType

/**
 * @author dan
 * @since version 1.0 on 3/19/15.
 */
abstract class MediaFile(){
  def path: File
  def fileType: FileType
}

object MediaFile {
  object FileType extends Enumeration {
    type FileType = Value
    val mp3, mp4, m4a, flac, ogg = Value
  }

  /** get FileType from readable existing file
    * @return a FileType value or None if the file doesn't have a matching FileType */
   private[mediafiles] def getFileType(path: File): Option[FileType] ={
    try{
      val fileName = path.getName
      val length = fileName.length
      val extensionStart = fileName lastIndexOf '.' match {
        case x if x == length => fileName.length
        case -1 => 0
        case x => x + 1 //skip dot
      }
      val extension = fileName.substring( extensionStart, fileName.length).toLowerCase
      Option(FileType.withName(extension))
    }
    catch {
      case e: Exception => None
    }
  }

}




