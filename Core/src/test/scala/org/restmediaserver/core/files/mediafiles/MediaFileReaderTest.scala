package org.restmediaserver.core.files.mediafiles

import java.io.File

import akka.actor.{ActorRef, Props}
import akka.pattern.ask
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

  test("When MediaFileReader is sent a valid file that is a Song, a song should be sent back"){ reader =>
    val songFile = Library.Music1.song3Mp3.path
    val f: Future[Any] = reader ? songFile
    val returnVal = Await.result(f, 5 seconds)
    returnVal match {
      case Some(song) => song.isInstanceOf[Song] shouldBe true
      case _ => fail("Song was not sent back")
    }
  }

  test("When MediaFileReader is is passed a file with a non song file extension it returns None"){ reader =>
    val badFile =  Library.Music1.songBadExtension.path
    val mediaFile = reader ? badFile
    val result = Await.result(mediaFile, 5 seconds)
    result match {
      case None => true
      case Some(_) => fail("something")
      case _ => fail("something else")
    }
  }

  test("When MediaFileReader is sent a nonexistent file it should reply with None"){ reader =>
    val songFile = new File("/nonExistentFile")
    val f = reader ?  songFile
    val result = Await.result(f, 5 seconds)
    result match {
      case None => true
      case _ => fail("result should be empty")
    }
  }
}
