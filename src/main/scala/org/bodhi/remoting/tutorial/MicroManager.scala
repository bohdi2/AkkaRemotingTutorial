package org.bodhi.remoting.tutorial

import akka.actor.{ActorSelection, Actor}

class MicroManager(worker: ActorSelection) extends Actor {
  def receive = {
    case Start =>
      println("MicroManager starts to nag")

      // MicroManager needs to lookup the worker. Again use a URI containing the
      // ActorSystem's and Actor's name.


      // Now send some messages to it
      worker ! Nag
      worker ! Nag

  }
}
