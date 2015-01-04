package org.bodhi.remoting.tutorial

import akka.actor.{Identify, ActorIdentity, ActorSystem}
import scala.concurrent.duration._
import scala.concurrent.Await
import akka.pattern.ask

class Manager150 {
  val conf = Profile.load("Manager150")
  
  val managerSystem = ActorSystem("managerSystem", conf)
  
  val remotePath = conf.getString("remotePath")

  val selection = managerSystem.actorSelection(remotePath)

  val future = selection.ask(Identify(remotePath))(5.seconds)

  val result = Await.result(future, 5.seconds).asInstanceOf[ActorIdentity]

  println("Looking for " + remotePath)
  println("found: " + result.ref)

}
