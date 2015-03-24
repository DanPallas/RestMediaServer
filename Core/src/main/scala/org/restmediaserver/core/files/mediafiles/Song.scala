package org.restmediaserver.core.files.mediafiles

import java.io.File

import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.datatype.Artwork
import org.jaudiotagger.tag.{FieldKey, Tag}
import org.restmediaserver.core.files.mediafiles.MediaFile.FileType
import org.restmediaserver.core.files.mediafiles.MediaFile.FileType.FileType

import scala.collection.JavaConverters._

/** Mutable because nothing should hold a reference to a file that is no longer valid. For example if the title is
 * updated then it should change for all references.
 * Created by Dan Pallas on 3/19/15.
 */
class Song(path: File,
                   fileType: FileType,
                   bitRate: Long,
                   channels: String,
                   encodingType: String,
                   format: String,
                   sampleRate: Int,
                   trackLength: Int,
                   vbr: Boolean,
                   title: Option[String],
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
                   albumArtist: Option[String],
                   isCompilation: Option[Boolean],
                   hasArtwork: Boolean
            ) extends MediaFile(path, fileType) {

  def trackLengthPrettyString: String = {
    val minutes = trackLength / Song.SecondsInMinute
    val seconds = trackLength % Song.SecondsInMinute
    s"$minutes:$seconds"
  }

  /** read list of artwork from music file */
  def readArtwork(): List[Artwork] = {
    if (hasArtwork) {
      val tag = AudioFileIO.read(this.path).getTag
      tag.getArtworkList.asScala.toList
    } else List[Artwork]()
  }

  /** read first artwork from music file */
  def readFirstArtwork(): Option[Artwork] = {
    if (hasArtwork) {
      val tag = Song.getOrCreateTag(this.path)
      Option(tag.getFirstArtwork)
    } else None
  }

}

object Song {
  private val SecondsInMinute = 60

  def apply(path: File): Option[Song] = {
    getFileType(path) match {
      case None => None
      case Some(fileType) => {
        val audioFile = AudioFileIO.read(path)
        val header = audioFile.getAudioHeader
        val tag = audioFile.getTagOrCreateAndSetDefault
        Option(new Song(path,
          fileType,
          header.getBitRateAsNumber,
          header.getChannels,
          header.getEncodingType,
          header.getFormat,
          header.getSampleRateAsNumber,
          header.getTrackLength,
          header.isVariableBitRate,
          tagFieldAsString(FieldKey.TITLE),
          tagFieldAsInt(FieldKey.TRACK),
          tagFieldAsInt(FieldKey.TRACK_TOTAL),
          tagFieldAsInt(FieldKey.DISC_NO),
          tagFieldAsInt(FieldKey.DISC_TOTAL),
          tagFieldAsString(FieldKey.ARTIST),
          tagFieldAsString(FieldKey.ALBUM),
          tagFieldAsInt(FieldKey.YEAR),
          tagFieldAsString(FieldKey.GENRE),
          tagFieldAsString(FieldKey.COMMENT),
          tagFieldAsString(FieldKey.COMPOSER),
          tagFieldAsString(FieldKey.ORIGINAL_ARTIST),
          tagFieldAsString(FieldKey.REMIXER),
          tagFieldAsString(FieldKey.CONDUCTOR),
          tagFieldAsInt(FieldKey.BPM),
          tagFieldAsString(FieldKey.GROUPING),
          tagFieldAsString(FieldKey.ISRC),
          tagFieldAsString(FieldKey.RECORD_LABEL),
          tagFieldAsString(FieldKey.ENCODER),
          tagFieldAsString(FieldKey.LYRICIST),
          tagFieldAsString(FieldKey.LYRICS),
          tagFieldAsString(FieldKey.ALBUM_ARTIST),
          Option(isCompilation()),
          hasArtwork()
        ))

        def tagFieldAsInt(key: FieldKey): Option[Int] = {
          try {
            // TODO potentially frequent exceptions from tag.getFirst, investigate
            val str = tag.getFirst(key)
            Option(str.toInt)
          } catch {
            case e: Exception => None
          }
        }

        def tagFieldAsString(key: FieldKey): Option[String] = {
          try {
            // TODO potentially frequent exceptions from tag.getFirst, investigate
            Option(tag.getFirst(key))
          } catch {
            case e: Exception => None
          }
        }

        def hasArtwork(): Boolean = {
          tag.getFirstArtwork match {
            case null => false
            case _ => true
          }
        }

        def isCompilation(): Boolean = {
          val falseStrings = Set("false", "f", "0") // lower case values that could mean false
          tagFieldAsString(FieldKey.IS_COMPILATION) match {
            case Some(str) => if (str.trim.length > 0 && !falseStrings.contains(str.toLowerCase)) true else false
            case None => false
          }
        }
      }
    }

  }

    /** get FileType from readable existing file
      * @return a FileType value or None if the file doesn't have a matching FileType */
    def getFileType(path: File): Option[FileType] ={
      try{
        val fileName = path.getName
        val extension = fileName.substring(fileName lastIndexOf '.', fileName.length).toLowerCase
        Option(FileType.withName(extension))
      }
      catch {
        case e: Exception => None
      }
    }
  /** Gets a tag for the file. If none exists, it is created, set, and returned. */
  private def getOrCreateTag(path: File): Tag = AudioFileIO.read(path).getTagOrCreateAndSetDefault
  }


