package org.restmediaserver.core.files.mediafiles

import java.io.File

import org.restmediaserver.core.files.mediafiles.MediaFile.FileType.FileType

/**
 * Created by dan on 3/19/15.
 */
abstract class MediaFile(path: File, fileType: FileType)

object MediaFile {
  object FileType extends Enumeration {
    type FileType = Value
    val mp3, mp4, m4v, flac, ogg = Value
  }
}




