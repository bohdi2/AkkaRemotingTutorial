package org.bodhi.remoting.tutorial

import akka.actor.{Actor, Props, ActorSystem}

class Worker150 {
  
  val conf = Profile.load("Worker150")
  val workerSystem = ActorSystem("workerSystem", conf)
  
  workerSystem.actorOf(Props(new ChattyWorker), "chattyWorker")


}

class ChattyWorker extends Actor {
  override def preStart(): Unit = println("ChattyWorker preStart")
  override def postStop(): Unit = println("ChattyWorker postStop")
  
  def receive = {
    case msg: Any => println("ChattyWorker got message: " + msg)
  }
}
