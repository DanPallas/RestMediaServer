package org.restmediaserver.core.library

/** Object representing a directory in the library and all of its immediate children
  * @author Dan Pallas
  * @since v1.0 on 4/12/15.
 */
case class LibraryFolder(children: Seq[LibraryFile])
object LibraryFolder {
  /** new empty LibraryFolder */
  def apply(): LibraryFolder = new LibraryFolder(Seq())
}

/** Simple object representing entry in library, but containing only the mod time and path of a file or folder which
  * is in the library
  * @author Dan Pallas
  * @since v1.0 on 04/12/2015
  */
case class LibraryFile(path: String, modTime: Long)
