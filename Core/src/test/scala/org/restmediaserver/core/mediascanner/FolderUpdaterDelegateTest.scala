package org.restmediaserver.core.mediascanner

import java.io.File

import akka.actor.{ActorSystem, Props}
import akka.testkit.{TestActorRef, TestKit}
import org.restmediaserver.core.files.mediafiles.Song
import org.restmediaserver.core.library.{LibraryFile, LibraryFolder, MediaLibrary}
import org.restmediaserver.core.mediascanner.FolderUpdaterDelegateTest.{LibraryMock, UpdaterMock}
import org.restmediaserver.core.testfixtures.LibraryFixture.Library.Music1
import org.restmediaserver.core.testfixtures.{FabricatedParent, MockActor}
import org.restmediaserver.core.testsettings.BaseTestSettings
import org.scalatest._

/**
 * Created by Dan Pallas on 5/2/15.
 */
class FolderUpdaterDelegateTest()
  extends TestKit(ActorSystem.create("FolderUpdaterDelegateTest"))
  with FunSuiteLike with BaseTestSettings with BeforeAndAfterAll {

  test("when all files are newer than existing files all files are updated"){
    val updaterPops = Props.create(classOf[UpdaterMock])
    val library: TestActorRef[LibraryMock] = TestActorRef[LibraryMock]
    val folderUpdaterProps = FolderUpdaterDelegate.props(Music1.path,library,updaterPops)
    val parent = TestActorRef(FabricatedParent.props(testActor, folderUpdaterProps),"parent")
    expectMsg(FolderUpdaterDelegate.Success(Music1.path))
  }

  override protected def afterAll(): Unit = TestKit.shutdownActorSystem(system)
}
object FolderUpdaterDelegateTest {
  class UpdaterMock extends MockActor{

    def handleSong3Flac(file: File): Unit = sendSuccess(file)

    def handleSong3M4a(file: File): Unit = sendSuccess(file)

    def handleSong3Mp3(file: File): Unit = sendSuccess(file)

    def handleSongOgg(file: File): Unit = sendSuccess(file)

    def sendSuccess(file: File): Unit = {
      sender ! MediaFileUpdater.Success(file)
    }

    /** all recieve case statments go here */
    override def testReceive(msg: Any): Unit = {
      msg match {
        case file: File =>
          file match {
            case f if f == Music1.song3Flac.path => handleSong3Flac(f)
            case f if f == Music1.song3M4a.path => handleSong3M4a(f)
            case f if f == Music1.song3Mp3.path => handleSong3Mp3(f)
            case f if f == Music1.song3Ogg.path => handleSongOgg(f)
            case f => unhandled(msg)
          }
        case _ => unhandled(msg)
      }
    }
  }
  class LibraryMock extends MockActor{

    def handleSong3Flac(song: Song): Unit = successfulPut(song)

    def handleSong3M4a(song: Song): Unit = successfulPut(song)

    def handleSong3Mp3(song: Song): Unit = successfulPut(song)

    def handleSong3Ogg(song: Song): Unit = successfulPut(song)

    def successfulPut(song: Song): Unit = {
      sender ! MediaLibrary.SuccessfulPut(song.path)
    }

    override def testReceive(msg: Any): Unit = {
      msg match {
        case MediaLibrary.GetLibraryFolderMsg(path) if path == Music1.path => handleGetLibraryFolder()
        case MediaLibrary.PutSongMsg(s) =>
          s match{
            case song if song == Music1.song3Flac => handleSong3Flac(song)
            case song if song == Music1.song3M4a => handleSong3M4a(song)
            case song if song == Music1.song3Mp3 => handleSong3Mp3(song)
            case song if song == Music1.song3Ogg => handleSong3Ogg(song)
            case _ => unhandled(msg)
          }
        case u => unhandled(msg)
      }
    }

    def handleGetLibraryFolder(): Unit = {
      sender ! LibraryFolder(Seq(
        LibraryFile(Music1.song3Flac.path.getPath, Music1.song3Flac.path.lastModified - 5),
        LibraryFile(Music1.song3M4a.path.getPath, Music1.song3M4a.path.lastModified - 5),
        LibraryFile(Music1.song3Mp3.path.getPath, Music1.song3Mp3.path.lastModified - 5),
        LibraryFile(Music1.song3Ogg.path.getPath, Music1.song3Ogg.path.lastModified - 5)
      ))
    }
  }


}
