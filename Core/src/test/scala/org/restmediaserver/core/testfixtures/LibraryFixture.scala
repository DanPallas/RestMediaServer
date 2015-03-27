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
        Option("song3"),
        Option(1),
        Option(10),
        Option(1),
        Option(2),
        Option("artist"),
        Option("album"),
        Option(2014),
        Option("Synthpop"),
        Option("these are comments"),
        Option("composer"),
        Option("origart"),
        Option("remixer"),
        Option("conducted"),
        Option(220),
        Option("art"),
        Option("isrc"),
        Option("published"),
        Option("someone"),
        Option("writer"),
        Option("the lyrics"),
        Option("album-artist"),
        Option(false),
        true)
    }
  }
}