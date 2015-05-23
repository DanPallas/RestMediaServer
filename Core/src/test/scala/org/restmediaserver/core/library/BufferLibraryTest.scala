package org.restmediaserver.core.library

import java.io.File

import org.restmediaserver.core.testfixtures.LibraryFixture
import org.restmediaserver.core.testfixtures.LibraryFixture.Library.Music1
import org.restmediaserver.core.testsettings.BaseTestSettings
import org.scalatest.FunSuite
import org.scalatest.Matchers._

import scala.collection.mutable
import scala.concurrent.Await

/**
 * @author Dan Pallas
 * @since v1.0 on 5/13/15.
 */
class BufferLibraryTest extends FunSuite with BaseTestSettings{
  test("when empty, getLibraryFolder returns future of None"){
    val lib = BufferLibrary()
    Await.result(lib.getLibraryFolder(Music1.path), waitTime) shouldBe None
  }

  test("when nonempty, getLibraryFolder for a folder not in the library returns future of None"){
    val lib = BufferLibrary()
    val res = lib.putMediaFile(Music1.song3Flac)
    Await.result(res, waitTime)
    Await.result(lib.getLibraryFolder(new File("other")), waitTime) shouldBe None
  }

  test("when nonempty, getLibraryFolder for a folderin the library returns future of LibraryFolder with children"){
    val lib = BufferLibrary()
    lib.contents = mutable.Buffer(Music1.song3Flac,Music1.song3M4a)
    val folder = Await.result(lib.getLibraryFolder(Music1.path), waitTime)
    folder.orNull.children should contain only
      (LibraryFile(Music1.song3Flac.path, Music1.song3Flac.modTime),
        LibraryFile(Music1.song3M4a.path, Music1.song3M4a.modTime))
  }

  test("getLibraryFolder uses MediaFile.modTime for its modTime"){
    val lib = BufferLibrary()
    val older = LibraryFixture.older(Music1.song3Flac)
    lib.contents += older
    val folder = Await.result(lib.getLibraryFolder(Music1.path), waitTime)
    folder.get.children(0).modTime shouldBe older.modTime
  }

  test("when putting a file that is not in the library it returns a future of true"){
    val lib = BufferLibrary()
    Await.result(lib.putMediaFile(Music1.song3Flac), waitTime) shouldBe true
    lib.contents should contain only Music1.song3Flac
  }

  test("when putting a file that has an older version in the library, it replaces the old version"){
    val lib = BufferLibrary()
    lib.contents += Music1.song3Flac
    val newer = LibraryFixture.newer(Music1.song3Flac)
    Await.result(lib.putMediaFile(newer), waitTime) shouldBe true
    lib.contents should contain only newer
  }

  test("when putting a file that has the same version in the library, it is not added"){
    val lib = BufferLibrary()
    lib.contents += Music1.song3Flac
    Await.result(lib.putMediaFile(Music1.song3Flac), waitTime) shouldBe false
    lib.contents.length shouldBe 1
  }

  test("when putting a file that has a newer version in the library, it is not added"){
    val lib = BufferLibrary()
    lib.contents += Music1.song3Flac
    val older = LibraryFixture.older(Music1.song3Flac)
    Await.result(lib.putMediaFile(older), waitTime) shouldBe false
    lib.contents should contain only Music1.song3Flac
  }

  test("When removing a file that does not exist, should not remove anything"){
    val lib = BufferLibrary()
    lib.contents += Music1.song3Flac
    Await.result(lib.removeMediaFile(Music1.song3M4a.path),waitTime) shouldBe false
    lib.contents should contain only Music1.song3Flac
  }

  test("When removing a file that is in the library, it should be removed"){
    val lib = BufferLibrary()
    lib.contents += Music1.song3Flac
    lib.contents += Music1.song3M4a
    Await.result(lib.removeMediaFile(Music1.song3M4a.path),waitTime) shouldBe true
    lib.contents should contain only Music1.song3Flac
  }
}