package org.bodhi.remoting.tutorial

class Config100 {
  
  val config = """
  |akka {
  |  loglevel = "WARNING"
  |  actor.provider = "akka.remote.RemoteActorRefProvider"
  |
  |  remote.netty.tcp {
  |    hostname = ${worker.hostname}
  |    port = ${worker.port}
  |  }
  |}
  """.stripMargin.loadConfig

  println("config: " + config.getConfig("akka.remote.netty.tcp"))
  //println("config: " + config.getConfig("profile"))
  println("config: " + config.getConfig("worker"))


}