package org.bodhi.remoting.tutorial

import akka.actor.Actor

class ChattyWorker extends Actor {
  override def preStart(): Unit = println("ChattyWorker preStart")
  override def postStop(): Unit = println("ChattyWorker postStop")

  def receive = {
    case msg: Any => println("ChattyWorker got message: " + msg)
  }
}
