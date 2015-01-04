package org.bodhi.remoting.tutorial

import akka.actor.{Actor, Props, ActorSystem}

class Worker150 {
  
  val conf = Profile.load("Worker150")
  val workerSystem = ActorSystem("workerSystem", conf)
  
  workerSystem.actorOf(Props(new ChattyWorker), "chattyWorker")


}
