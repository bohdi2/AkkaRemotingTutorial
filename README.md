Akka Remote Tutorial
======================

This is a tutorial about using Akka remote actors. I hope that by taking a different approach from 
 existing tutorials that I can create something helpful.
 
I assume you know Scala, SBT, and the basics of Akka. If so then we're both on the same page. I am
 not an expert, just trying to learn this stuff as are you.
 
#Introduction

##Stupid Stuff

 In my examples I have actors for Managers and Workers. I picked these roles and names because I don't
 like names like "ActorA" or "ActorSystem3".

 The example code files are given names like "Worker150" and "Manager200". Workers and Managers
 with the same numeric ending are meant to work together. So "Worker340" and "Manager340" go together. The
 numbers don't mean anything.
 
##Location Transparency and Environment

The Akka people have worked hard to make Akka remoting as simple as possible. For the most part very
little of your code will need to change when you covert over. That is the good news. The bad news is
that how you think about your code and design may change, and that may lead to code changes. This should
not be surprising...

It is possible to run multiple remote actors in a single JVM and many of the examples on the web do exactly
that. I think that is confusing as hell and I'm not taking that approach. You will need to open two
terminal windows and invoke `sbt` from them. Some times you may want to run the examples on separate 
machines and in this case you have two options: run `sbt` on both machines, or copy the uber jar and run
it directly. In my case my other machine is a Raspberry Pi and invoking `sbt` on it is painfully slow so
I just copy the uber jar.

The code has a primitive concept of profiles. The default is to use the "loopback" profile which uses
127.0.0.1 as the host ip for both the worker and manager systems. You can change the profile that is used
by overriding the "profiles.profile" configuration. An example will be shown below. There are three profiles
defined in the file common.conf:

```
profiles {
  loopback {
    manager.hostname = "127.0.0.1"
    manager.port = 5001

    worker.hostname = "127.0.0.1"
    worker.port = 5000
  }

  local {
    manager.hostname = "192.168.1.8"
    manager.port = 5001

    worker.hostname = "192.168.1.8"
    worker.port = 5000
  }

  foo {
    manager.hostname = "192.168.0.0.1"
    manager.port = 5001

    worker.hostname = "192.168.0.1"
    worker.port = 5000
  }

  profile = loopback // Default
}
```
    
The "local" profile is for running the worker and manager on the same machine using their real IP addresses. The
"foo" profile is for running the worker and manager on separate machines. In both cases you should edit
the files and use your IP addresses.

##Running the Code

I am using Scala 2.11.4, Akka 2.3.4, and SBT 0.13.7 on a Ubuntu desktop and on Raspberry Pi's.

Follow these steps to run the code:

1. Open two terminals. I'll refer to them as left and right.
2. In the left terminal `cd` to the _AkkaRemotingTutorial_ directory.
3. Type `sbt 'run W150'`. On my desktop I get this output and you should see something similar:
<pre>
> sbt 'run W150'
[info] Loading project definition from /home/chris/projects/akka/AkkaRemotingTutorial/project
[info] Set current project to Tutorial (in build file:/home/chris/projects/akka/AkkaRemotingTutorial/)
[info] Running org.bodhi.remoting.tutorial.Main W150
Main W150
ChattyWorker preStart
</pre>
4. In the right teminal `cd` to the _AkkaRemotingTutorial_ directory.
5. Type `sbt 'run M150'`. On my desktop I get this output and you should see something similar.
<pre>
> sbt 'run M150'
[info] Loading project definition from /home/chris/projects/akka/AkkaRemotingTutorial/project
[info] Set current project to Tutorial (in build file:/home/chris/projects/akka/AkkaRemotingTutorial/)
[info] Running org.bodhi.remoting.tutorial.Main M150
Main M150
Looking for akka.tcp://workerSystem@127.0.0.1:5000/user/chattyWorker
found: Some(Actor[akka.tcp://workerSystem@127.0.0.1:5000/user/chattyWorker#537855532])
</pre>
If you get warnings and errors about invalid associations and connections refused then there is
a configuration problem (or my code is wrong).


#Lesson 150

##Worker150
The Worker150 is just an ActorSystem with one actor (ChattyWorker). When ChattyWorker gets a
message it echos it to stdout. Here is Worker150.scala:

```scala
package org.bodhi.remoting.tutorial

import akka.actor.{Props, ActorSystem}

class Worker150 {

  // Load worker's configuration
  val conf = Profile.load("Worker150")
  println("conf: " + conf.getConfig("akka.remote.netty.tcp"))

  // Create ActorSystem and add a "chattyWorker" who echoes all messages he gets
  // to stdout.
  val workerSystem = ActorSystem("workerSystem", conf)
  workerSystem.actorOf(Props(new ChattyWorker), "chattyWorker")
}
```

And here is ChattyWorker.scala

```scala
package org.bodhi.remoting.tutorial

import akka.actor.Actor

class ChattyWorker extends Actor {
  override def preStart(): Unit = println("ChattyWorker preStart")
  override def postStop(): Unit = println("ChattyWorker postStop")

  def receive = {
    case msg: Any => println(s"ChattyWorker got message: $msg")
  }
}
```

When you run the Worker150 program its single Actor (ChattyWorker) waits to receive messages. 
But wait a minute, where will these messages come from? In our case they will come from the
Manger150 program running in its own JVM possibly on another machine. First let's take a look at 
Worker150.conf configuration file.

```
akka {
  loglevel = "WARNING"

  actor {
    provider = "akka.remote.RemoteActorRefProvider"
  }
  
  remote.netty.tcp {
    hostname = ${worker.hostname}
    port = ${worker.port}
  }
}
```

There are a few things to notice

1. The "akka.actor.provider" is set to use RemoteActorRefProvider instead of the default 
LocalActorRefProvider. The RemoteActorRefProvider performs Akka magic to allow one instance 
of Akka talk to another instance. How this works is way beyond the scope of this tutorial.
2. There is a "akka.remote" section. This section controls how our "workerSystem" will run and how it will
appear to other remote nodes. In this case it says that "workerSystem" lives on the ${worker.hostname} 
and will listen to connections from other Akka nodes on port ${worker.port}
3. The ${worker.hostname} and ${worker.port} will resolve to a specific profile. If you're using the
default loopback profile their values will be 127.0.0.1 and 5000.

It is worth emphasizing that the "akka.remote" section defines how other akka instances will connect
to the Worker150 program. The "akka.remote" section is not a list of remote systems that Worker150 will
connect to. 

###Output
When you run the program you should get output similar to this:
<pre>
> sbt 'run W150'
[info] Loading project definition from /home/chris/projects/akka/AkkaRemotingTutorial/project
[info] Set current project to Tutorial (in build file:/home/chris/projects/akka/AkkaRemotingTutorial/)
[info] Running org.bodhi.remoting.tutorial.Main W150
Main W150
ChattyWorker preStart
</pre>

The last line "ChattyWorker preStart" is printed by ChattyWorker when it starts. At this point we know
that the worker is alive and waiting for messages.

##Manager150

Here is Manager150.scala 

```scala
package org.bodhi.remoting.tutorial

import akka.actor.ActorSystem
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class Manager150 {
  // Load Manager's configuration
  val conf = Profile.load("Manager150")
  
  // Construct the remote path to the ChattyWorker.
  val hostname = conf.getString("worker.hostname")
  val port = conf.getString("worker.port")
  
  val workerPath = s"akka.tcp://workerSystem@$hostname:$port/user/chattyWorker"
  
  // Create an ActorSystem and ask it for the ActorRef corresponding to the
  // workerPath we just created. This is complicated by the need to
  // use ActorSystem.actorSelection() and because it is possible (very possible)
  // that the remote chattyWorker we're looking for does not exist. Why might
  // it not exist? Perhaps you forgot to start the worker program?
  
  val managerSystem = ActorSystem("managerSystem", conf)
  
  val selection = managerSystem.actorSelection(workerPath)

  // Once we have a ActorSelection we need to get an Actor from it. There are a
  // few ways to do this. I'm using resolveOne() which returns a Future and I
  // just wait for it to finish. If it returns correctly (it found the ChattyWorker)
  // then I send "Hello" to the worker; else I print an error.
  selection.resolveOne(5.seconds).onComplete {
    case Success(actor) =>
      println(s"Found $actor")
      actor ! "Hello"

    case Failure(ex) =>
      println(s"Did not find anything for $workerPath")
  }
}
```

Let's start by taking a closer look at the workerPath

```
  // Construct the remote path to the ChattyWorker.
  val hostname = conf.getString("worker.hostname")
  val port = conf.getString("worker.port")
  
  val workerPath = s"akka.tcp://workerSystem@$hostname:$port/user/chattyWorker"
  ```

The Manager150 program is going to connect to the Worker150 program that is running in its own
JVM (possibly on a different machine). So Manager150 needs to know where Worker150 is and what specific
ActprSystem and Actor on Worker150 it wants to use. Well we know that Worker150 is on the machine at 
IP ${worker.hostname} and that it is listening to port ${worker.port} (we know this because these values
are defined in Profiles.conf). We also know that the ActorSystem used by Worker150 is called "workerSystem"
and that the Actor is named "chattyWorker". Putting these items together we create "remotePath" which is
a URI. On my desktop running with the loopback profile I get the value:

```
akka.tcp://workerSystem@127.0.0.1:5000/user/chattyWorker
```

A few additional comments. Why "akka.tcp:" and why "/usr/chattyWorker"? Akka supports multiple socket
protocols such as TCP and UDP and others. I'm using TCP, I've never tried UDP or any others. I'm
sure they are a lot of fun. If you just use "akka:" instead of "akka.tcp:" the program won't work. 

We need to prefix "chattyWorker" with "/user" because (to quote the Akka documents) 

>"/user" is the
>guardian actor for all user-created top-level actors; actors created using ActorSystem.actorOf are
>found below this one.

The next step is to create an ActorSystem for the Manager150 program.
```
  val managerSystem = ActorSystem("managerSystem", conf)
  
  val selection = managerSystem.actorSelection(workerPath)
```

The two consoles should ... When the action stops, stop each system by pressing
Ctrl-C.


Problems?
---------

If you're having any problems with this code, edit the _application.conf_
file in the _src/main/resources_ directory of each project, and remove the
comments from the debug-related lines.

More Information
----------------




