package org.restmediaserver.core.mediascanner

import java.io.File

import org.restmediaserver.core.files.mediafiles.MediaFile.FileType
import org.restmediaserver.core.files.mediafiles.{AsyncMediaFileService, MediaFile, Song}
import org.restmediaserver.core.library.CallStoringBufferLibrary
import org.restmediaserver.core.testfixtures.LibraryFixture
import org.restmediaserver.core.testfixtures.LibraryFixture.Library.Music1
import org.restmediaserver.core.testsettings.BaseTestSettings
import org.scalatest.Matchers._
import org.scalatest.{Outcome, fixture}

import scala.concurrent.Await

/**
 * @author Dan Pallas
 * @since v1.0 on 5/18/15.
 */
class MediaRootTest extends fixture.FunSuite with BaseTestSettings {
  case class FixtureParam(lib: CallStoringBufferLibrary, asmf: AsyncMediaFileService)

  override protected def withFixture(test: OneArgTest): Outcome = {
    val lib = CallStoringBufferLibrary()
    val asmf = AsyncMediaFileService()
    val fixture = FixtureParam(lib, asmf)
    withFixture(test.toNoArgTest(fixture))
  }

  test("When Library is empty scan adds all media files in root folder to path"){ f =>
    val root = MediaRoot(Music1.path,f.lib,f.asmf)
    Await.result(root.startScan(), waitTime) shouldBe 4
    f.lib.contents should contain allOf (Music1.song3Flac, Music1.song3M4a, Music1.song3Mp3, Music1.song3Ogg)
    f.lib.contents.size shouldBe 4
    f.lib.putMediaFileCalls.size shouldBe 4
  }

  test("scan does not add files which already exist in lib with same modTime"){ f =>
    f.lib.contents += Music1.song3Flac
    val root = MediaRoot(Music1.path,f.lib,f.asmf)
    Await.result(root.startScan(), waitTime) shouldBe 3
    f.lib.putMediaFileCalls should not contain Music1.song3Flac
    f.lib.putMediaFileCalls.size shouldBe 3
  }

  test("scan does not add files which already exist in lib with newer modTime"){ f =>
    f.lib.contents += LibraryFixture.newer(Music1.song3Flac)
    val root = MediaRoot(Music1.path,f.lib,f.asmf)
    Await.result(root.startScan(), waitTime) shouldBe 3
    f.lib.putMediaFileCalls should not contain Music1.song3Flac
    f.lib.putMediaFileCalls.size shouldBe 3
  }

  test("scan puts files which already exist in lib with older modTime"){ f =>
    f.lib.contents += LibraryFixture.older(Music1.song3Flac)
    val root = MediaRoot(Music1.path,f.lib,f.asmf)
    Await.result(root.startScan(), waitTime) shouldBe 4
    f.lib.putMediaFileCalls should contain (Music1.song3Flac)
    f.lib.putMediaFileCalls.size shouldBe 4
  }

  test("scan removes files when they no longer exist on the file system"){ f =>
    val toRemove = Song(new File(Music1.path, "child"), FileType.flac, 1, None, 1, "1", "ds", "sadf", 1, 1, true,
      "title", None, None, None, None, "artist", "atitle", None, "genre", "comment", "comp", "oa", "rem", "cond",
      None, "group", "isrc", "label", "enc", "lyracist", "Lyrics", "aa", true, true
    )
    f.lib.contents += LibraryFixture.older(toRemove)
    val root = MediaRoot(Music1.path,f.lib,f.asmf)
    Await.result(root.startScan(), waitTime) shouldBe 5
    f.lib.removeMediaFileCalls should contain (toRemove.path)
    f.lib.removeMediaFileCalls.size shouldBe 1
  }

  test("scan does not add files for which fileFilter returns false"){ f =>
    val root = MediaRoot(Music1.path,f.lib,f.asmf, f => MediaFile.getFileType(f).get != MediaFile.FileType.flac)
    Await.result(root.startScan(), waitTime) shouldBe 3
    f.lib.contents should not contain Music1.song3Flac
    f.lib.contents.size shouldBe 3
    f.lib.putMediaFileCalls.size shouldBe 3
  }

  test("scan removes files from the library if they no longer exist on the disk"){ f=>
    val root = MediaRoot(Music1.path, f.lib, f.asmf)
    f.lib.contents += Music1.song3DoesNotExist
    Await.result(root.startScan(), waitTime)
    f.lib.contents should not contain Music1.song3DoesNotExist
  }


}
