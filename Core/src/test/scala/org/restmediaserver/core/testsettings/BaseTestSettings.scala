package org.restmediaserver.core.testsettings

import java.util.logging.{Level, Logger}

/** Holds general test settings
 * @author Dan Pallas
 * @since v1.0  on 3/28/15.
 */
trait BaseTestSettings {
  Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF)
}
