package org.restmediaserver.core.files.mediafiles

import java.io.File

import org.restmediaserver.core.testfixtures.LibraryFixture.Library
import org.scalatest.{FunSuite, Matchers}

/**
 * Test Song Class
 * @author Dan Pallas
 * @since  version 1.0 on 3/23/15.
 */
class SongTest extends FunSuite with Matchers {

  test("Song.apply should read in an mp3 and return a Song"){
//    val songFile: File = fileFromTestResource("/musicFiles/library/music1/song3.mp3")
    val songFile = Library.Music1.songMp3.path
    val song = Song(songFile)
    song should not be empty
  }

  test("Song.apply should return empty option if passed a nonexistant file."){
    val songFile: File = new File("/nonExistentFile")
    val song = Song(songFile)
    song shouldBe empty
  }

  test("When Song.apply is passed a file with a non song file extension it returns None"){
//    val badFile = fileFromTestResource("/musicFiles/library/music1/song.badExtension")
    val badFile =  Library.Music1.songBadExtension.path
    val song = Song(badFile)
    song shouldBe empty
  }

  test("When Song.apply is passed a file with no extension it should return None"){
//    val badFile = fileFromTestResource("/musicFiles/library/music1/mp3")
    val badFile = Library.Music1.mp3.path
    val song = Song(badFile)
    song shouldBe empty
  }

  test("When Song.apply is passed an mp3 all tags should be read in correctly"){
    val songFixture = Library.Music1.songMp3
    val maybeSong = Song(songFixture.path)
    maybeSong match {
      case None => maybeSong should not be empty
      case Some(song) => song shouldBe songFixture
    }

  }

  //todo test that tags are successfully read

}

object SongTest {
  private def fileFromTestResource(path: String): File = {
    new File(getClass.getResource(path).toURI)
  }
}
