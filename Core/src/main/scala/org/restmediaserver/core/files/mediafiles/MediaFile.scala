package org.restmediaserver.core.files.mediafiles

import java.io.File

import com.typesafe.scalalogging.LazyLogging
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.{Tag, FieldKey}
import org.jaudiotagger.tag.datatype.Artwork
import org.restmediaserver.core.files.mediafiles.MediaFile.FileType.FileType
import scala.collection.JavaConverters.asScalaBufferConverter

import scala.util.matching.Regex

/**
 * @author dan
 * @since version 1.0 on 3/19/15.
 */
sealed trait MediaFile{
  def path: String
  def fileType: FileType
  protected val file = new File(path)
  def parent = file.getParent

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
    Song(file)
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

  /** return a pred that returns true if the passed MediaFile is a child of the path parent
    * @param parent path of parent directory. Should not have trailing '/'
    * @return function which returns true iff MediaFile is a child of the path parent */
  def isChild(parent: String): MediaFile => Boolean = {
    val regex = parent + File.separator + ".*"
    val pattern = new Regex(regex).pattern
    (mf: MediaFile) => pattern.matcher(mf.path).matches()
  }
}



case class Song private(override val path: String,
                        override val fileType: FileType,
                        override val modTime: Long,
                        override val id: Option[Int],
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
      val tag = AudioFileIO.read(file).getTag
      tag.getArtworkList.asScala.toList
    } else List[Artwork]()
  }

  /** read first artwork from music file */
  def readFirstArtwork(): Option[Artwork] = {
    if (hasArtwork) {
      val tag = Song.getOrCreateTag(this.file)
      Option(tag.getFirstArtwork)
    } else None
  }
}

object Song extends LazyLogging {
  private val SecondsInMinute = 60
  private val FalseStrings = Set("false", "f", "0", "no") // lower case values that could mean false
  def apply(
             file: File,
             fileType: FileType,
             modTime: Long,
             id: Option[Int],
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
             ) = {
    new Song(
      file.getAbsolutePath,
      fileType,
      modTime,
      id,
      bitRate,
      channels,
      encodingType,
      format,
      sampleRate,
      trackLength,
      vbr,
      title,
      trackNumber,
      trackTotal,
      discNumber,
      discTotal,
      artist,
      albumTitle,
      year,
      genre,
      comment,
      composer,
      originalArtist,
      remixer,
      conductor,
      bpm,
      grouping,
      isrc,
      recordLabel,
      encoder,
      lyracist,
      lyrics,
      albumArtist,
      isCompilation,
      hasArtwork
    )
  }
  def apply(path: File): Option[Song] = {
    MediaFile.getFileType(path) match {
      case None => None
      case Some(fileType) =>
        val audioFile = AudioFileIO.read(path)
        val header = audioFile.getAudioHeader
        implicit val tag = audioFile.getTagOrCreateAndSetDefault
        Option(
          new Song(
            path.getAbsolutePath,
            fileType,
            path.lastModified(),
            None,
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



