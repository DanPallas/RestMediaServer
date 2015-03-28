package org.restmediaserver.core.testfixtures

import java.io.File
import java.net.URI

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
    lazy val directory = getClass.getResource("/musicFiles/library").toString

    object Music1 {
      val path = directory + "/music1"

      val mp3 = new FileFixture {
        val path = new File(new URI(Music1.path + "/mp3"))
      }

      val songBadExtension = new FileFixture {
        val path: File = new File(new URI(Music1.path + "/song.badExtension"))
      }
      val songMp3 = new Song(
        new File(new URI(Music1.path + "/song3.mp3")),
        FileType.mp3,
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
      val songM4a = new Song(
        new File(new URI(Music1.path + "/song3.m4a")),
        FileType.m4a,
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
        new File(new URI(Music1.path + "/song3.ogg")),
        FileType.ogg,
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
        new File(new URI(Music1.path + "/song3.flac")),
        FileType.flac,
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