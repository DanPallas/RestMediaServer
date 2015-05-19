package org.restmediaserver.core.testsettings

import java.util.concurrent.Executors
import java.util.logging.Logger

import org.restmediaserver.core.Properties

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/** Holds general test settings
 * @author Dan Pallas
 * @since v1.0  on 3/28/15.
 */
trait BaseTestSettings {
  def setup() = Logger.getLogger("org.jaudiotagger").setLevel(Properties.jatLogLevel)
  implicit val ec = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(20))
  val waitTime = Duration(5, SECONDS)
  setup()
}
