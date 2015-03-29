package org.restmediaserver.core.testsettings

import java.util.logging.Logger

import org.restmediaserver.core.Properties

/** Holds general test settings
 * @author Dan Pallas
 * @since v1.0  on 3/28/15.
 */
trait BaseTestSettings {
  Logger.getLogger("org.jaudiotagger").setLevel(Properties.jatLogLevel)
}
