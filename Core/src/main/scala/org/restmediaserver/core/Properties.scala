package org.restmediaserver.core

import java.util.logging.Level

import com.typesafe.scalalogging.LazyLogging

/** All access to properties file(s) should come through this class
  * @author Dan Pallas
  * @since v1.0 on 3/28/15
 */
object Properties extends LazyLogging{
  /** logging level for jaudio tagger's build in logging. */
  private val JatLogLevelKey = "jaudiotagger.logging.level"
  /** path to main properties file */
  private val ApplicationPropertiesFile = "/application.properties"
  private val props = loadProperties()

  /** JAudioTagger logging Level */
  def jatLogLevel: Level = {
    val str = props.getProperty(JatLogLevelKey,"OFF").toUpperCase
    try {
      Level.parse(str)
    }
    catch {
      case e: IllegalArgumentException =>
        logger.warn(s"illegal value for property $JatLogLevelKey. Setting to 'off'")
        Level.OFF
      case e: Throwable => throw e
    }
  }

  private def loadProperties(): java.util.Properties = {
    val props = new java.util.Properties()
    val propFile = getClass.getResourceAsStream(ApplicationPropertiesFile)
    props.load(propFile)
    props
  }

}