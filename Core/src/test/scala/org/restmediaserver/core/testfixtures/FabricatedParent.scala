package org.restmediaserver.core.testfixtures

import akka.actor.{ActorRef, Actor, Props}

/** Proxy for testing parent child relationships. Creates actor for childProps and forwards messages recieved to child
  * and forwards messages received from child to testActor.
 * Created by Dan Pallas on 4/29/15.
 */
class FabricatedParent(testActor: ActorRef, childProps: Props) extends Actor {
  val child = context.actorOf(childProps)

  override def receive: Receive = {
    case msg if sender == child => testActor forward msg
    case msg => child forward msg
  }
}
object FabricatedParent {
  def props(testActor: ActorRef, childProps: Props) = Props(classOf[FabricatedParent], testActor, childProps)
}
