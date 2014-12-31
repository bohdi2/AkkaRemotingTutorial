package org.bodhi.remoting.tutorial

import akka.actor.{Identify, ActorSystem, Props}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import scala.concurrent.Await
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

// At this stage of the tutorial the code is very contrived. We create two ActorSystems (Managers
// and Workers). We add a MicroManager to Managers, and a SadWorker to Workers. We lookup
// the MicroManager by his URI and send him a START message. He looks up the SadWorker and NAGs
// him.
// In the next stage of the tutorial we'll make the Managers and Workers remote ActorSystems.


class Tutorial100 {

  // Create a system of workers
  val workers = ActorSystem("workerSystem", ConfigFactory.parseString("""
    akka {
    loglevel = "INFO"
      actor {
        provider = "akka.actor.LocalActorRefProvider"
      }
    }
  """))

  workers.actorOf(Props(new SadWorker), "sadWorker")

  // Create a manager actor system
  val managers = ActorSystem("managerSystem", ConfigFactory.parseString("""
    akka {
    loglevel = "INFO"
      actor {
        provider = "akka.actor.LocalActorRefProvider"
      }
    } 
  """))
  
  val worker = workers.actorSelection("akka://workerSystem/user/sadWorker")
  println("worker: " + worker)

 
  // Now we need to send a Start message to the micro manager. We look him up using a URI
  // containing the ActorSystem and Actor names.
  
  managers.actorOf(Props(new MicroManager(worker)), "microManager")
  val manager = managers.actorSelection("akka://managerSystem/user/microManager")
  println("manager: " + manager)

  implicit val timeout = Timeout(5 seconds)
  val remotePath = "akka://managerSystem/user/microManager"
  val selection = managers.actorSelection(remotePath)

  println("remotePath: " + remotePath)
  println("selection: " + selection)
  val future = selection ? Identify(remotePath)
  val result = Await.result(future, timeout.duration)
  println("Hello: " + result)

  manager ! Start
  
  // Sleep a little while and then die.
  Thread.sleep(500)
 
  managers.shutdown()
  workers.shutdown()
}

