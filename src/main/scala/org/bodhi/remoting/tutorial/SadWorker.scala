package org.bodhi.remoting.tutorial

import akka.actor.Actor

class SadWorker extends Actor {
  def receive = {
    //case Nag => println("Ugh...")
    case _ => println("unexpected...")
  }
}
