package org.bodhi.remoting.tutorial

import org.bodhi.remoting.tutorial.Profile._

class Config100 {
  
  val config = """
  akka {
    loglevel = "WARNING"
    actor {
      provider = "akka.remote.RemoteActorRefProvider"
    }

    remote.netty.tcp {
      hostname = ${profile.worker.hostname}
      port = ${profile.worker.port}
    }
  }
  """.loadConfig

  println("config: " + config.getConfig("akka.remote.netty.tcp"))
  println("config: " + config.getConfig("profile"))
  
}