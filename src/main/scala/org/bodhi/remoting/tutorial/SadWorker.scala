package org.bodhi.t0

import akka.actor.Actor

/**
 * Created by chris on 12/29/14.
 */
class SadWorker extends Actor {
  def receive = {
    //case Nag => println("Ugh...")
    case _ => println("unexpected...")
  }
}
