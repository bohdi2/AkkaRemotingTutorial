package org.bodhi.t0

import akka.actor.{Identify, ActorSystem, Props}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import scala.concurrent.Await
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

// At this stage of the tutorial the code is still very contrived but we've added the necessary 
// configuration to support remoting.  
//   Create two ActorSystems (Managers and Workers).
//   Add a MicroManager to Managers, and a SadWorker to Workers.
//   Lookup the MicroManager by his URI and send him a START message.
//   The MicroManager looks up the SadWorker and NAGs him.
// In the next stage of the tutorial we'll separate the Managers and Workers into separate processes.


class Manager200 {

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
      }
    }
  """))
 
  implicit val timeout = Timeout(5 seconds)
  val remotePath = "akka.tcp://workerSystem@127.0.0.1:5150/user/idiotWorker"
  val selection = managers.actorSelection(remotePath)
  
  println("--- remotePath: " + remotePath)
  println("--- selection: " + selection)
  val future = selection.resolveOne()
  //val future = selection ? Identify(remotePath)
  val result = Await.result(future, timeout.duration)
  println("--- Hello: " + result)
  //val worker = managers.actorOf(Props(classOf[MicroManager], remotePath), "microManager")
  
  //println("worker:  " + worker)

 
  // Now we need to send a Start message to the micro manager. We look him up using a URI
  // containing the ActorSystem and Actor names.

  //managers.actorOf(Props(new MicroManager(worker)), "microManager")
  //val manager = managers.actorSelection("akka.tcp://managerSystem@127.0.0.1:5051/user/microManager")
  //println("manager: " + manager)

  //manager ! Start

  // Sleep a little while and then die.
  //Thread.sleep(500)
  managers.shutdown()
}




