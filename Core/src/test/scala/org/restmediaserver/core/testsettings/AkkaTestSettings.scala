package org.restmediaserver.core.testsettings

import akka.actor.ActorSystem
import akka.util.Timeout
import scala.concurrent.duration._

/** Trait with settings for classes using akka actors and futures in tests
  * @author Dan Pallas
  * @since v1.0 on 4/25/15. */
trait AkkaTestSettings extends BaseTestSettings{
  override def setup() = {
    super.setup()
  }
  val TestSystem = "TestSystem"
  val system = ActorSystem(TestSystem)
  implicit val timeout = Timeout(5 seconds)
  implicit val ec = system.dispatcher

  setup()
}