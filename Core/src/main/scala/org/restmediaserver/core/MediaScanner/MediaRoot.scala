package org.restmediaserver.core.mediascanner

import akka.actor.Actor
import org.restmediaserver.core.library.MediaLibrary

import scala.concurrent.Future
import scala.reflect.io.File

/** Actor for scanning a media folder for changes and updates and keeping a Library in sync with the media files in
  * this folder
  * @author Dan Pallas
  * @since v1.0 on 4/12/15.
 */
abstract class MediaRoot(path: File, library: MediaLibrary) extends Actor{
  /** begin scanning media files in this folder in a new thread. This means doing an initial recursive scan of
    * mediafiles in this folder * to see if any changes have occured since the last time this folder was scanned.
    * responds with a scanCompleteMsg when scan finishes successfully or scanFailedMsg when failed. Asking with this
    * message should use a long timeout as this operation will likely take awhile. */
  case class StartScanMsg()
  case class ScanCompleteMsg()
  case class ScanExceptiondMsg(ex: Throwable)

  /** watch for modifications, additions, or deletions occurring to media files in this directory. Any changes
    * found in these mediafiles will be reflected in the Library. This method will not return until stop has been
    * called **/
  case class watchMsg()


  def initialScan(): Future[Unit] = ???

  def receive = ???
}
