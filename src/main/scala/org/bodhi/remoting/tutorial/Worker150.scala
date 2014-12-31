package org.bodhi.remoting.tutorial

import akka.actor.{Actor, Props, ActorSystem}
import com.typesafe.config.ConfigFactory

class Worker150 {
  val workers = ActorSystem("workerSystem", ConfigFactory.parseString("""
    akka {
      //loglevel = "DEBUG"
      //log-config-on-start = on
      actor {
        provider = "akka.remote.RemoteActorRefProvider"
      }
      remote {
        enabled-transports =  ["akka.remote.netty.tcp"]
        netty.tcp {
          hostname = "127.0.0.1"
          port = 5050
        }

        log-received-messages = on
      }
      debug {
        //autoreceive = on
        //life-cycle = on
      }
    }
  """))

  val w = workers.actorOf(Props(new IdiotWorker), "idiotWorker")
  println("waiting for messages")
  w ! "Hello"
  println("waiting for messages")


}

class IdiotWorker extends Actor {
  def receive = {
    case msg: Any => println("duh... " + msg)
  }
}
