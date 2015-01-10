package org.bodhi.remoting.tutorial

import akka.actor.ActorSystem
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.util.{Failure, Success}

class Manager150 {
  val config = """
  |akka {
  |  loglevel = "WARNING"
  |
  |  actor.provider = "akka.remote.RemoteActorRefProvider"
  |
  | remote.netty.tcp {
  |   hostname = ${manager.hostname}
  |   port = ${manager.port}
  | }
  |}
  |
  |workerPath = "akka.tcp://workerSystem@"${worker.hostname}":"${worker.port}"/user/chattyWorker"
  |
  """.stripMargin.loadConfig

  val workerPath = config.getString("workerPath")
  println(workerPath)

  // Create an ActorSystem and ask it for the ActorRef corresponding to the
  // workerPath we just created. This is complicated by the need to
  // use ActorSystem.actorSelection() and because it is possible (very possible) that the
  // remote chattyWorker we're looking for does not exist. Why might it not exist? Perhaps
  // you forgot to start the worker program?
  
  val managerSystem = ActorSystem("managerSystem", config)
  
  val selection = managerSystem.actorSelection(workerPath)

  // Once we have a ActorSelection we need to get an Actor from it. There are a few ways to do
  // this. I'm using resolveOne() which returns a Future and I just wait for it to finish.
  // If it returns correctly (it found the ChattyWorker) then I send "Hello" to the worker; otherwise
  // I print an error.
  selection.resolveOne(5.seconds).onComplete {
    case Success(actor) =>
      println(s"Found $actor")
      actor ! "Hello"

    case Failure(ex) =>
      println(s"Did not find anything for $workerPath")
  }
}

