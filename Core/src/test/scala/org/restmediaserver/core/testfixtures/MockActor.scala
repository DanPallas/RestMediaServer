package org.restmediaserver.core.testfixtures

import akka.actor.Actor
import org.restmediaserver.core.ActorMessage

import scala.collection.mutable

/**
 * Mock actors should extend this class. It stores the messages that are received. They can be accessed direclty or
 * through the GetMessages message.
 * @author Dan Pallas
 * @since v1.0 on 5/2/15
 */
abstract class MockActor extends Actor{
  val messages = mutable.Buffer[Any]()

  /** all recieve case statments go here */
  def testReceive(msg: Any): Unit

  override def receive: Receive = {
    case GetMessages => sender ! messages
    case msg =>
      messages += msg
      testReceive(msg)
  }

  override def unhandled(message: Any): Unit ={
    throw new BadMessage(message)
  }

}
/** get messages Buffer. Will not itself be added to messages list */
case class GetMessages() extends ActorMessage
/** An unhandled message was received by a mock actor */
case class BadMessage(message: Any) extends Exception(message.toString)