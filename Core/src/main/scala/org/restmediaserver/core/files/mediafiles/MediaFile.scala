package org.restmediaserver.core.files.mediafiles

import java.io.File

import org.jaudiotagger.tag.datatype.Artwork
import org.restmediaserver.core.files.mediafiles.MediaFile.FileType.FileType

/**
 * Created by dan on 3/19/15.
 */
abstract class MediaFile(path: File, title: String, fileType: FileType) extends File(path.getPath)
object MediaFile {
  object FileType extends Enumeration {
    type FileType = Value
    val mp3, mp4, m4v, flac, ogg = Value
  }

}


/** Mutable because nothing should hold a reference to a file that is no longer valid. For example if the title is
 * updated then it should change for all references.
 * Created by Dan Pallas on 3/19/15.
 */
class Song(path: File,
                   title: String,
                   fileType: FileType,
                   bitRate: Int,
                   channels: String,
                   encodingType: String,
                   format: String,
                   sampleRate: Int,
                   trackLength: Int,
                   vbr: Boolean,
                   trackNumber: Option[Int],
                   trackTotal: Option[Int],
                   discNumber: Option[Int],
                   discTotal: Option[Int],
                   artist: Option[String],
                   albumTitle: Option[String],
                   year: Option[Int],
                   genre: Option[String],
                   comment: Option[String],
                   composer: Option[String],
                   originalArtist: Option[String],
                   remixer: Option[String],
                   conductor: Option[String],
                   bpm: Option[Int],
                   grouping: Option[String],
                   isrc: Option[String],
                   recordLabel: Option[String],
                   encoder: Option[String],
                   lyracist: Option[String],
                   lyrics: Option[String],
                   hasArtwork: Boolean
            ) extends MediaFile(path: File, title, fileType) {
  def trackLengthPrettyString: String = "" //todo
  def getArtwork: List[Artwork] = List() //todo
  def getFirstArtwork: Option[Artwork] = None //todo
}

object Song {
  def apply(path: File): Option[Song] = {
    path.exists() && path.canRead match {
      case false => None
      case true => None //Todo

    }


    def getFileExtension(path: File): String ={
      val fileName = path.getName
      val extension = fileName.substring(fileName lastIndexOf '.', fileName.length)
      extension.toLowerCase
    }
  }
}