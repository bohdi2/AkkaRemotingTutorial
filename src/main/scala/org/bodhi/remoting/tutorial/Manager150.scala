package org.bodhi.t0

import akka.actor.{Identify, ActorIdentity, ActorRef, ActorSystem}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._
import scala.concurrent.Await
import akka.pattern.ask

class Manager150 {
  // Create a manager actor system
  val managers = ActorSystem("managerSystem", ConfigFactory.parseString("""
    akka {
      //loglevel = "DEBUG"
      actor {
        provider = "akka.remote.RemoteActorRefProvider"
      }
      remote {
        enabled-transports =  ["akka.remote.netty.tcp"]
        netty.tcp {
          hostname = "127.0.0.1"
          port = 5051
        }
        log-sent-messages = on
      }
      debug {
        autoreceive = on
        //life-cycle = on
      }}
  """))


  implicit val timeout = Timeout(5 seconds)
  val remotePath = "akka.tcp://workerSystem@127.0.0.1:5050/user/idiotWorker"
  val selection = managers.actorSelection(remotePath)

  println("--- Looking for " + remotePath)
  
  val future = selection ? Identify(remotePath)

  val result = Await.result(future, timeout.duration).asInstanceOf[ActorIdentity]
  result.ref.foreach(r => r ? "Hello2")
  println("--- result: " + result)
  
  println("sleeping...")
  Thread.sleep(5000)
  managers.shutdown()
}
