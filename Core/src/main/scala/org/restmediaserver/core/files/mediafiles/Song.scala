package org.restmediaserver.core.files.mediafiles

import java.io.File

import com.typesafe.scalalogging.LazyLogging
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.datatype.Artwork
import org.jaudiotagger.tag.{FieldKey, Tag}
import org.restmediaserver.core.files.mediafiles.MediaFile.FileType.FileType

import scala.collection.JavaConverters._

/** Mutable because nothing should hold a reference to a file that is no longer valid. For example if the title is
 * updated then it should change for all references.
 * Created by Dan Pallas on 3/19/15.
 */
case class Song(override val path: File,
           override val fileType: FileType,
                   bitRate: Long,
                   channels: String,
                   encodingType: String,
                   format: String,
                   sampleRate: Int,
                   trackLength: Int,
                   vbr: Boolean,
                   title: String,
                   trackNumber: Option[Int],
                   trackTotal: Option[Int],
                   discNumber: Option[Int],
                   discTotal: Option[Int],
                   artist: String,
                   albumTitle: String,
                   year: Option[Int],
                   genre: String,
                   comment: String,
                   composer: String,
                   originalArtist: String,
                   remixer: String,
                   conductor: String,
                   bpm: Option[Int],
                   grouping: String,
                   isrc: String,
                   recordLabel: String,
                   encoder: String,
                   lyracist: String,
                   lyrics: String,
                   albumArtist: String,
                   isCompilation: Boolean,
                   hasArtwork: Boolean
            ) extends MediaFile {

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

object Song extends LazyLogging {
  private val SecondsInMinute = 60
  private val FalseStrings = Set("false", "f", "0", "no") // lower case values that could mean false
  def apply(path: File): Option[Song] = {
    MediaFile.getFileType(path) match {
      case None => None
      case Some(fileType) =>
        val audioFile = AudioFileIO.read(path)
        val header = audioFile.getAudioHeader
        implicit val tag = audioFile.getTagOrCreateAndSetDefault
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
          isCompilation(),
          hasArtwork()
        ))
    }

  }

  private def hasArtwork()(implicit tag: Tag): Boolean = {
    tag.getFirstArtwork match {
      case null => false
      case _ => true
    }
  }

  private def isCompilation()(implicit tag: Tag): Boolean = {
    val str: String = tagFieldAsString(FieldKey.IS_COMPILATION)
    if (str.trim.length > 0 && !FalseStrings.contains(str.toLowerCase)) true else false
  }

  private def tagFieldAsInt(key: FieldKey)(implicit tag: Tag): Option[Int] = {
    try {
      val str = tag.getFirst(key)
      Option(str.toInt)
    } catch {
      case e: NumberFormatException =>
        logger.warn(s"Unable to parse field $key from Song '${tag.getFirst(FieldKey.TITLE)}' by " +
          s"'${tag.getFirst(FieldKey.ARTIST)}' as Integer")
        None
      case e: Throwable =>
        throw e
    }
  }

  def tagFieldAsString(key: FieldKey)(implicit tag: Tag): String = {
      val str = tag.getFirst(key)
      str
  }

  /** Gets a tag for the file. If none exists, it is created, set, and returned. */
  private def getOrCreateTag(path: File): Tag = AudioFileIO.read(path).getTagOrCreateAndSetDefault
}


