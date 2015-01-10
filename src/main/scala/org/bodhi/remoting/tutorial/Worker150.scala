package org.bodhi.remoting.tutorial

import akka.actor.{Props, ActorSystem}

class Worker150 {

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

  // Create ActorSystem and add a "chattyWorker" who echoes all messages he gets
  // to stdout.
  val workerSystem = ActorSystem("workerSystem", config)
  workerSystem.actorOf(Props(new ChattyWorker), "chattyWorker")
}
