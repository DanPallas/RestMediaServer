package org.restmediaserver.core.utilities.libraryutilities

import akka.actor.Actor
import org.restmediaserver.core.library.MediaLibrary

/**
 * Created by Dan Pallas on 4/23/15.
 */
class FileUpdater(library: MediaLibrary) extends Actor{
  
  override def receive: Receive = ???
  
  private class FileUpdaterDelegate extends Actor{
    override def receive: Actor.Receive = ???
  }
}
