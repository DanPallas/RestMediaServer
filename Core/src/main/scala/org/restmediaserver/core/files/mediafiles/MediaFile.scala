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

  /** mod date of the file which this information came from. If it came from a file, then it's the mod date of the file
    * when this object was created from it. If it came from the library, then it's the mod date that the media file had
    * when it was read into this object */
  def modTime: Long
  /** library ID */
  def id: Option[Int]
}

object MediaFile {
  object FileType extends Enumeration {
    type FileType = Value
    val mp3, mp4, m4a, flac, ogg = Value
  }

  /** returns a media file of the appropriate type or nothing if the file could not be read or wasn't a media file */
  def apply(file: File): Option[MediaFile] = {
    // if videos are implemented this will need to check filetype to decide which apply to call
    Song(file);
  }

  /** get FileType from readable existing file
    * @return a FileType value or None if the file doesn't have a matching FileType */
    def getFileType(path: File): Option[FileType] ={
    try{
      val fileName = path.getName
      val extensionStart = fileName lastIndexOf '.' match {
        case x if x == fileName.length => fileName.length
        case -1 => return None
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




