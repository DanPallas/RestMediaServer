package org.restmediaserver.core.files.mediafiles

import java.io.File

import org.scalatest.{FunSuite,Matchers}

/**
 * Created by Dan Pallas on 3/23/15.
 */
class SongTest extends FunSuite with Matchers {
  test("Song.apply should read in an mp3 and return a Song"){
    val songFile: File = SongTest.fileFromTestResource("/musicFiles/library/music1/song3.mp3")
    val song = Song(songFile)
    song should not be empty
  }

  test("Song.apply should return empty option if passed an invalid file."){
    val songFile: File = new File("nonExistentFile")
    val song = Song(songFile)
    song shouldBe empty
  }

  //todo test for wrong extension
  //todo test that tags are successfully read

}

object SongTest {
  private def fileFromTestResource(path: String): File = {
    new File(getClass.getResource(path).toURI)
  }
}
