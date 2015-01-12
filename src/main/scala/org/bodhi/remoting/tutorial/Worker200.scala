package org.bodhi.remoting.tutorial

import akka.actor.{Props, ActorSystem}

class Worker200 {

  val config = """
  |akka {
  |  loglevel = "WARNING"
  |
  |  actor.provider = "akka.remote.RemoteActorRefProvider"
  |
  |  remote.netty.tcp {
  |    hostname = ${worker.hostname}
  |    port = ${worker.port}
  |  }
  |}
  """.stripMargin.loadConfig

  // Create ActorSystem.
  val workerSystem = ActorSystem("workerSystem", config)
}
