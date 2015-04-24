package org.restmediaserver.core.files.mediafiles

import java.io.File

import org.restmediaserver.core.testfixtures.LibraryFixture.Library
import org.restmediaserver.core.testsettings.BaseTestSettings
import org.scalatest.{Matchers, FunSuite}

/**
 * Created by Dan Pallas on 4/23/15.
 */
class MediaFileTest extends FunSuite with Matchers with BaseTestSettings {
  test("When given a valid file that is a song, a song should be returned"){
    val songFile = Library.Music1.song3Mp3.path
    val returnVal = MediaFile(songFile)
    returnVal match {
      case None => fail()
      case Some(shouldBeSong) => shouldBeSong.isInstanceOf[Song] shouldBe true
    }
  }

  test("When MediaFile.apply is passed a file with a non song file extension it returns None"){
    //    val badFile = fileFromTestResource("/musicFiles/library/music1/song.badExtension")
    val badFile =  Library.Music1.songBadExtension.path
    val mediaFile = MediaFile(badFile)
    mediaFile shouldBe empty
  }

  test("When MediaFile.apply is passed a nonexistent path it should return None"){
    val songFile = new File("/nonExistentFile")
    val mediaFile = MediaFile(songFile)
    mediaFile shouldBe empty
  }

}
