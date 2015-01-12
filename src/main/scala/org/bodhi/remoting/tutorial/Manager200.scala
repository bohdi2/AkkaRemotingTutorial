package org.bodhi.remoting.tutorial

import akka.actor.{Props, ActorSystem}

class Manager200 {
  val config = """
  |akka {
  |  loglevel = "WARNING"
  |  //loglevel = "DEBUG"
  |
  |  actor.provider = "akka.remote.RemoteActorRefProvider"
  |
  |  actor.deployment {
  |    "/chattyWorker" {
  |      remote = "akka.tcp://workerSystem@"${worker.hostname}":"${worker.port}
  |    }
  |  }
  |
  | remote.netty.tcp {
  |   hostname = ${manager.hostname}
  |   port = ${manager.port}
  | }
  |}
  |
  """.stripMargin.loadConfig
  
  val managerSystem = ActorSystem("managerSystem", config)

  val actor = managerSystem.actorOf(Props[ChattyWorker], "chattyWorker")
  
  actor ! "Hello Remote Worker"
}

