package org.restmediaserver.core.files.mediafiles

import java.io.File

import scala.concurrent.{ExecutionContext, Future}

/** Class for bulkheading asynchronous MediaFile operations
  * @author Dan Pallas
  * @since v1.0 on 5/17/15
 */
class AsyncMediaFileService()(implicit val ec: ExecutionContext) {
  /** reads in a new MediaFile from a java file */
  def read(path: File): Future[Option[MediaFile]] = {
    Future(MediaFile(path))
  }
}
object AsyncMediaFileService {
  def apply()(implicit ec: ExecutionContext): AsyncMediaFileService = new AsyncMediaFileService()(ec)
}