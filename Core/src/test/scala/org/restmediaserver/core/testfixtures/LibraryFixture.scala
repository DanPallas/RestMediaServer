package org.restmediaserver.core.testfixtures

import java.io.{FileFilter, File}
import java.nio.file.Paths

import org.restmediaserver.core.files.mediafiles.MediaFile.FileType
import org.restmediaserver.core.files.mediafiles.{MediaFile, Song}

/**
 * @author Dan Pallas
 * @since v1.0 on 3/25/15.
 */

object LibraryFixture {

  trait FileFixture {
    def path: File
  }

  object Library {
    lazy val directory: File = Paths.get(this.getClass.getResource("/musicFiles/library").toURI).toAbsolutePath.toFile

    object Music1 {
      val path = new File(directory, "music1")

      val mp3 = new FileFixture {
        val path = new File(Music1.path, "mp3")
      }

      val songBadExtension = new FileFixture {
        val path: File = new File(Music1.path, "song.badExtension")
      }
      val song3Mp3 = Song(
        new File(Music1.path, "song3.mp3"),
        FileType.mp3,
        new File(Music1.path, "song3.mp3").lastModified(),
        None,
        128,
        "Mono",
        "mp3",
        "MPEG-1 Layer 3",
        44100,
        0,
        false,
        "song3",
        Option(1),
        Option(10),
        Option(1),
        Option(2),
        "artist",
        "album",
        Option(2014),
        "Synthpop",
        "these are comments",
        "composer",
        "origart",
        "remixer",
        "conducted",
        Option(220),
        "art",
        "isrc",
        "published",
        "someone",
        "writer",
        "the lyrics",
        "album-artist",
        false,
        true)
      val song3M4a = Song(
        new File(Music1.path, "song3.m4a"),
        FileType.m4a,
        new File(Music1.path, "song3.m4a").lastModified(),
        None,
        2,
        "1",
        "AAC",
        "AAC",
        44100,
        0,
        true,
        "song3",
        Option(1),
        Option(10),
        Option(1),
        Option(2),
        "artist",
        "album",
        Option(2014),
        "SynthPop",
        "these are comments",
        "composer",
        "",
        "",
        "conducted",
        Option(220),
        "art",
        "isrc",
        "",
        "Nero AAC codec / 1.5.4.0",
        "writer",
        "the lyrics",
        "album-artist",
        false,
        true
      )

      val song3Ogg = Song(
        new File(Music1.path, "song3.ogg"),
        FileType.ogg,
        new File(Music1.path, "song3.ogg").lastModified(),
        None,
        96,
        "1",
        "Ogg Vorbis v1",
        "Ogg Vorbis v1",
        44100,
        0,
        true,
        "song3",
        Option(1),
        None,
        Option(1),
        None,
        "artist",
        "album",
        Option(2014),
        "Synthpop",
        "these are comments",
        "composer",
        "",
        "",
        "conducted",
        Option(220),
        "",
        "isrc",
        "",
        "Xiph.Org libVorbis I 20120203 (Omnipresent)",
        "writer",
        "the lyrics",
        "album-artist",
        false,
        true
      )

      val song3Flac = Song(
        new File(Music1.path, "song3.flac"),
        FileType.flac,
        new File(Music1.path, "song3.flac").lastModified(),
        None,
        0,
        "1",
        "FLAC 16 bits",
        "FLAC 16 bits",
        44100,
        0,
        true,
        "song3",
        Option(1),
        None,
        Option(1),
        None,
        "artist",
        "album",
        Option(2014),
        "Synthpop",
        "these are comments",
        "composer",
        "",
        "",
        "conducted",
        Option(220),
        "",
        "isrc",
        "",
        "reference libFLAC 1.2.1 20070917",
        "writer",
        "the lyrics",
        "album-artist",
        false,
        true
      )

      val song3DoesNotExist = Song(
        new File(Music1.path, "song3.Doesnt.Exist.flac"),
        FileType.flac,
        new File(Music1.path, "song3.flac").lastModified(),
        None,
        0,
        "1",
        "FLAC 16 bits",
        "FLAC 16 bits",
        44100,
        0,
        true,
        "song3",
        Option(1),
        None,
        Option(1),
        None,
        "artist",
        "album",
        Option(2014),
        "Synthpop",
        "these are comments",
        "composer",
        "",
        "",
        "conducted",
        Option(220),
        "",
        "isrc",
        "",
        "reference libFLAC 1.2.1 20070917",
        "writer",
        "the lyrics",
        "album-artist",
        false,
        true
      )

    }

    object Music2{
      lazy val path = new File(directory, "music2")
      lazy val mediaFiles = {
        def getFiles(path: File): IndexedSeq[File] = {
          val mfs = path.listFiles(new FileFilter {
            override def accept(pathname: File): Boolean = MediaFile.getFileType(pathname) isDefined
          })
          val childDirs = path.listFiles(new FileFilter {
            override def accept(pathname: File): Boolean = pathname isDirectory
          })
          val childMfs = childDirs flatMap getFiles
          mfs.toVector ++ childMfs
        }
        val opts = getFiles(path) map (MediaFile(_)) filter (_.isDefined)
        opts map (_.get)
      }
    }
  }

  /** returns a song with an earlier modTime than the input */
  def older(song: Song): Song = {
    Song(new File(song.path),
      song.fileType,
      song.modTime - 1000,
      song.id,
      song.bitRate,
      song.channels,
      song.encodingType,
      song.format,
      song.sampleRate,
      song.trackLength,
      song.vbr,
      song.title,
      song.trackNumber,
      song.trackTotal,
      song.discNumber,
      song.discTotal,
      song.artist,
      song.albumTitle,
      song.year,
      song.genre,
      song.comment,
      song.composer,
      song.originalArtist,
      song.remixer,
      song.conductor,
      song.bpm,
      song.grouping,
      song.isrc,
      song.recordLabel,
      song.encoder,
      song.lyracist,
      song.lyrics,
      song.albumArtist,
      song.isCompilation,
      song.hasArtwork)
  }

  /** returns a song with a later modTime than the input */
  def newer(song: Song): Song = {
    Song(new File(song.path),
      song.fileType,
      song.modTime + 1000,
      song.id,
      song.bitRate,
      song.channels,
      song.encodingType,
      song.format,
      song.sampleRate,
      song.trackLength,
      song.vbr,
      song.title,
      song.trackNumber,
      song.trackTotal,
      song.discNumber,
      song.discTotal,
      song.artist,
      song.albumTitle,
      song.year,
      song.genre,
      song.comment,
      song.composer,
      song.originalArtist,
      song.remixer,
      song.conductor,
      song.bpm,
      song.grouping,
      song.isrc,
      song.recordLabel,
      song.encoder,
      song.lyracist,
      song.lyrics,
      song.albumArtist,
      song.isCompilation,
      song.hasArtwork)
  }
}

