package org.restmediaserver.core.files.mediafiles

import java.io.File

import akka.actor.{ActorRef, Props}
import akka.pattern.ask
import org.restmediaserver.core.files.mediafiles.MediaFileReader.Failed
import org.restmediaserver.core.testfixtures.LibraryFixture.Library
import org.restmediaserver.core.testsettings.AkkaTestSettings
import org.scalatest.{Matchers, fixture}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/** Tests for MediaFileReader
  * @author Dan Pallas
  * @since v1.0 on 4/25/15.
 */
class MediaFileReaderTest extends fixture.FunSuite with Matchers with AkkaTestSettings{
  type FixtureParam = ActorRef

  def withFixture(test: OneArgTest) = {
    val reader = system.actorOf(Props[MediaFileReader])
    try{
      test(reader)
    } finally {
      system.stop(reader)
    }
  }

  test("When MediaFileReader is sent a valid file that is a Song, a song should be sent back") { reader =>
    val songFile = Library.Music1.song3Mp3.path
    val f: Future[Any] = reader ? songFile
    val returnVal = Await.result(f, 5 seconds)
    returnVal.isInstanceOf[Song] shouldBe true
  }

  test("When MediaFileReader is is passed a file with a non song file extension it returns Failed message with sent " +
    "file"){
    reader =>
    val badFile =  Library.Music1.songBadExtension.path
    val mediaFile = reader ? badFile
    val result = Await.result(mediaFile, 5 seconds)
    result match {
      case Failed(path) => path shouldBe badFile
      case _ => fail("should have failed")
    }
  }

  test("When MediaFileReader is sent a nonexistent file it should reply with Failed(path)"){ reader =>
    val songFile = new File("/nonExistentFile")
    val f = reader ?  songFile
    val result = Await.result(f, 5 seconds)
    result match {
      case Failed(path) => path shouldBe songFile
      case _ => fail("result was not Failed(path)")
    }
  }
}
