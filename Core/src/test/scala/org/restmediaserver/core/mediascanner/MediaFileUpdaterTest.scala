package org.restmediaserver.core.mediascanner

import java.io.File

import akka.actor._
import akka.testkit.{TestActorRef, TestKit}
import org.restmediaserver.core.ActorMessage
import org.restmediaserver.core.files.mediafiles.MediaFileReader
import org.restmediaserver.core.library.MediaLibrary
import org.restmediaserver.core.testfixtures.{FabricatedParent, LibraryFixture}
import org.restmediaserver.core.testsettings.BaseTestSettings
import org.scalatest.fixture
import org.scalatest.Matchers._

import scala.collection.mutable
/**
 * @author Dan Pallas
 * @since v1.0 on 4/27/15.
 */
class MediaFileUpdaterTest(_system: ActorSystem) extends TestKit(_system)
      with fixture.FunSuiteLike with BaseTestSettings {
  def this() = this(ActorSystem("MediaFileUpdaterTest"))
  class ReaderMock extends Actor {
    val received = mutable.Buffer[File]()
    override def receive: Receive = {
      //readable
      case file: File if file == LibraryFixture.Library.Music1.song3M4a.path =>
        received += file
        sender !  LibraryFixture.Library.Music1.song3M4a
        // readable
      case file: File if file == LibraryFixture.Library.Music1.song3Flac.path =>
        received += file
        sender ! LibraryFixture.Library.Music1.song3Flac
        //unreadable
      case file: File =>
        received += file
        sender ! MediaFileReader.Failed(file)
    }
  }

  class LibraryMock extends Actor {
    val received = mutable.Buffer[ActorMessage]()
    override def receive: Actor.Receive = {
      case msg: ActorMessage =>
        received += msg
        msg match {
          case MediaLibrary.PutSongMsg(song) =>
            // successfully put
            if(song == LibraryFixture.Library.Music1.song3M4a)
              sender ! MediaLibrary.SuccessfulPut(song.path)
            else if(song == LibraryFixture.Library.Music1.song3Flac)
              // already been put
              sender ! MediaLibrary.NotPutOlder(song.path)
          case badMessage => unhandled(badMessage)
        }
    }
  }

  case class F(reader: TestActorRef[ReaderMock],
               library: TestActorRef[LibraryMock],
               updaterParent: ActorRef)
  override type FixtureParam = F

  override protected def withFixture(test: OneArgTest) = {
    val reader: TestActorRef[ReaderMock] = TestActorRef(new ReaderMock)
    val library = TestActorRef(new LibraryMock)
    val updaterProps = Props(classOf[MediaFileUpdater], library,reader)
    val parent = system.actorOf(Props(classOf[FabricatedParent], testActor, updaterProps))
    try{
      test(F(reader, library, parent))
    } finally {
      system.stop(reader)
      system.stop(library)
      system.stop(parent)
    }
  }

  test("when MediaFileUpdater is sent a valid song file it should read the file, update, and return success"){
    f =>
      val song = LibraryFixture.Library.Music1.song3M4a
      f.updaterParent ! song.path
      expectMsg(MediaFileUpdater.Success(song.path))

      val reader = f.reader.underlyingActor
      reader.received.length shouldBe 1
      reader.received should contain (song.path)

      val library = f.library.underlyingActor
      library.received.length shouldBe 1
      library.received should contain (MediaLibrary.PutSongMsg(song))
  }

  test("when MediaFileUpdater is sent a bad path it should return a failure"){
    f =>
      val badfile = new File("badfile")
      f.updaterParent ! badfile
      expectMsg(MediaFileUpdater.Failed(badfile))

      val reader = f.reader.underlyingActor
      reader.received.length shouldBe 1
      reader.received should contain (badfile)

      val library = f.library.underlyingActor
      library.received.length shouldBe 0
  }

  test("when MediaFileUpdater receives a valid song and the library responds that it is too old, MediaFileUpdater " +
    "responds with Success"){
    f =>
      val song = LibraryFixture.Library.Music1.song3Flac
      f.updaterParent ! song.path
      expectMsg(MediaFileUpdater.Success(song.path))

      val reader = f.reader.underlyingActor
      reader.received.length shouldBe 1
      reader.received should contain (song.path)

      val library = f.library.underlyingActor
      library.received.length shouldBe 1
      library.received should contain (MediaLibrary.PutSongMsg(song))
  }


}
