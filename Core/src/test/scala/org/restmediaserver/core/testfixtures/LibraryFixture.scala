package org.restmediaserver.core.testfixtures

import java.io.File
import java.nio.file.Paths

import org.restmediaserver.core.files.mediafiles.MediaFile.FileType
import org.restmediaserver.core.files.mediafiles.Song

/**
 * @author Dan Pallas
 * @since v1.0 on 3/25/15.
 */

object LibraryFixture {
  trait FileFixture{
    def path: File
  }

  object Library {
    lazy val directory: File = Paths.get(this.getClass.getResource("/musicFiles/library").toURI).toFile

    object Music1 {
      val path = new File(directory, "music1")

      val mp3 = new FileFixture {
        val path = new File(Music1.path, "mp3")
      }

      val songBadExtension = new FileFixture {
        val path: File = new File(Music1.path, "song.badExtension")
      }
      val song3Mp3 = new Song(
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
      val song3M4a = new Song(
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

      val song3Ogg = new Song(
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

      val song3Flac = new Song(
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


    }
  }
}