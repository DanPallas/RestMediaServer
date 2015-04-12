package org.restmediaserver.core.mediascanner

import org.restmediaserver.core.library.MediaLibrary

import scala.reflect.io.File

/** Object for scanning a media folder for changes and updates and keeping a Library in sync with the media files in
  * this folder
  * @author Dan Pallas
  * @since v1.0 on 4/12/15.
 */
abstract class MediaRoot {
  def path: File
  def library: MediaLibrary
  /** begin scanning media files in this folder. This means doing an initial recursive scan of mediafiles in this folder
    * to see if any changes have occured since the last time this folder was scanned and then watching for
    * modifications, additions, or deletions occurring to media files in this directory. Any changes found in these
    * mediafiles will be reflected in the Library. This method will not return until stop has been called.
    * @return true if process executed and exited successfully. Otherwise false.
    */
  def scan: Boolean
  /** stop scanning this folder */
  def stop: Unit
}
