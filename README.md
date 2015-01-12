Akka Remote Tutorial
======================

This is a tutorial about Akka remote actors. I've taken a different approach from
 existing tutorials and I hope that it is helpful.
 
I assume you know Scala, SBT, and the basics of Akka. If you do then we're both on the same page. I am
 not an expert, just someone trying to learn this stuff as are you.
 
#Introduction

 In my examples I have actors for Managers and Workers. I picked these roles and names because I don't
 like names such as "ActorA" or "ActorSystem3".

 The example code files are given names like "Worker150" and "Manager200". Workers and Managers
 with the same numeric ending are meant to work together. So "Worker340" and "Manager340" go together.
 
 You will need to open two terminal windows and invoke `sbt` from them. You will run Workers in one
 terminal, and Managers in the other. It is easiest to run the Worker and Manager programs on the same
 machine, but you can also run them on separate machines. I suggest that you try this since running on
 multiple machines is point of remoting. When running on multiple machines you have two options for 
 starting the programs: run `sbt` on both machines, or copy the uber jar to one of the machines 
 and run it directly. In my case my other machine is a Raspberry Pi and invoking `sbt` on the Pi is 
 painfully slow so I just copy the uber jar to it and run it with `java -jar`. I'll explain the steps 
 in detail further on.

The code has a primitive concept of profiles. The default is to use the "loopback" profile which sets the
host ip to 127.0.0.1 for both the worker and manager systems. You can change the profile that is used
by overriding the "profileName" system property. An example will be shown below. There are three profiles
defined: `loopback` that uses 127.0.01 , `local` that you can edit to use your non loopback IP, and 
`rpi1` which you can edit for running on two machines. The profiles are defined in the resource directory
as loopback.conf, local.conf, and rpi1.conf. Here's loopback.conf

```
//loopback.conf
manager.hostname = "127.0.0.1"
manager.port = 5001

worker.hostname = "127.0.0.1"
worker.port = 5000
```
    
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
4. In the right terminal `cd` to the _AkkaRemotingTutorial_ directory.
5. Type `sbt 'run M150'`. On my desktop I get this output and you should see something similar.
<pre>
> sbt 'run M150'
[info] Loading project definition from /home/chris/projects/akka/AkkaRemotingTutorial/project
[info] Set current project to Tutorial (in build file:/home/chris/projects/akka/AkkaRemotingTutorial/)
[info] Running org.bodhi.remoting.tutorial.Main M150
Main M150
akka.tcp://workerSystem@127.0.0.1:5000/user/chattyWorker
Found Actor[akka.tcp://workerSystem@127.0.0.1:5000/user/chattyWorker#199825620]
</pre>
If you get warnings and errors about invalid associations and connections refused then there is
a configuration problem (or my code is wrong).


#Lesson 150

Simple example of remote Actor lookup. The Worker150 program starts first. When Manager150 starts it gets
a remote reference to Worker150's Actor and sends it a "Hello" message.

##Worker150
The Worker150 is just an ActorSystem with one actor (ChattyWorker). When ChattyWorker gets a
message it echos it to stdout. Here is Worker150.scala:

```scala
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
```

When you run the Worker150 program its single Actor (ChattyWorker) waits to receive messages,
but where will those messages come from? In our case they will come from the
Manger150 program running in its own JVM possibly on another machine. But first let's take a look at 
Worker150's configuration.

```scala
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
```

There are a few things to notice about the code fragment:

1. Typically the configuration would go into a resource file, but for clarity I'm including it in the
Scala code. The `loadConfig` is an implicit defined in Package.scala.
2. The `${worker.hostname}` and `${worker.port}` take their values from the appropriate profile. Dig into
the code if you want to learn how it works. If you're using the default loopback profile their values 
will be 127.0.0.1 and 5000.

And a few things to notice about the configuration itself:

1. The "akka.actor.provider" is set to use RemoteActorRefProvider instead of the default 
LocalActorRefProvider. The RemoteActorRefProvider performs Akka magic to allow one instance 
of Akka talk to another instance. How this works is way beyond the scope of this tutorial.
2. The "akka.remote" section controls how our ActorSystem will run and how it will
appear to other remote Akka nodes. In this case our ActorSystem will live on
${worker.hostname} and it will listen to connections from other Akka nodes on port ${worker.port}

It is worth emphasizing that the "akka.remote" section defines how other akka instances will connect
to the Worker150 program. It is not a list of remote systems that Worker150 will
connect to. 

The rest of the program simply creates an ActorSystem and adds the ChattyWorker to it.

```scala
  // Create ActorSystem and add a "chattyWorker" who echoes all messages he gets
  // to stdout.
  val workerSystem = ActorSystem("workerSystem", config)
  workerSystem.actorOf(Props(new ChattyWorker), "chattyWorker")
```

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

The last line "ChattyWorker preStart" indicates ChattyWorker is alive and waiting for messages.

##Manager150

Here is Manager150.scala 

```scala
package org.bodhi.remoting.tutorial

import akka.actor.ActorSystem
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.util.{Failure, Success}

class Manager150 {
  val config = """
  |akka {
  |  loglevel = "WARNING"
  |
  |  actor.provider = "akka.remote.RemoteActorRefProvider"
  |
  | remote.netty.tcp {
  |   hostname = ${manager.hostname}
  |   port = ${manager.port}
  | }
  |}
  |
  |workerPath = "akka.tcp://workerSystem@"${worker.hostname}":"${worker.port}"/user/chattyWorker"
  |
  """.stripMargin.loadConfig

  val workerPath = config.getString("workerPath")
  println(workerPath)

  // Create an ActorSystem and ask it for the ActorRef corresponding to the
  // workerPath. This is complicated by the need to
  // use ActorSystem.actorSelection() and because it is possible (very possible) that the
  // remote chattyWorker we're looking for does not exist.

  val managerSystem = ActorSystem("managerSystem", config)

  val selection = managerSystem.actorSelection(workerPath)

  // Once we have a ActorSelection we need to get an Actor from it. There are a few ways to do
  // this. I'm using resolveOne() which returns a Future and I just wait for it to finish.
  // If it returns correctly (it found the ChattyWorker) then I send "Hello" to the worker; otherwise
  // I print an error.
  selection.resolveOne(5.seconds).onComplete {
    case Success(actor) =>
      println(s"Found $actor")
      actor ! "Hello"

    case Failure(ex) =>
      println(s"Did not find anything for $workerPath")
  }
}
```

Let's start by taking a closer look at the workerPath defined in the configuration.

```
workerPath = "akka.tcp://workerSystem@"${worker.hostname}":"${worker.port}"/user/chattyWorker"
  ```

We want the Manager150 program to connect to the Worker150 program. So Manager150 needs to know where 
Worker150 is and the names of the
ActprSystem and Actor to use. Well, we know that Worker150 is on the machine at
IP `${worker.hostname}` and that it is listening to port `${worker.port}` (we know this because these values
are defined in the profile). We also know that the ActorSystem used by Worker150 is called "workerSystem"
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

The next step is to create an ActorSystem and lookup the worker.
```scala
  val managerSystem = ActorSystem("managerSystem", conf)
  
  val selection = managerSystem.actorSelection(workerPath)
```

Under the hood Akka has used the information in `workerPath` to find the worker's host and ask it for
a reference to the ChattyWorker. The final piece of code gets an ActorRef from the ActorSelection. The
method `ActorSelection.resolveOne` returns a Future. The `onComplete` waits for the Future to complete
or fail. If the future completes I print that the ActorRef was found, and I send the Actor a "Hello". If
the Future fails I print out an error.

```scala
  selection.resolveOne(5.seconds).onComplete {
    case Success(actor) =>
      println(s"Found $actor")
      actor ! "Hello"

    case Failure(ex) =>
      println(s"Did not find anything for $workerPath")
  }
```

##Discussion

There are a few variations that are interesting.

###Two Masters, One Worker

* I start M150 and W150 on my desktop using the `local` profile. I see that the manager finds the
worker and sends him a "Hello".
* I start another M150 on my raspberry pi using the `manager_on_rpi1` profile. This manager
finds the same worker and sends it a "Hello" too. Here's the worker's output:

```
ChattyWorker preStart
ChattyWorker got message: Hello
ChattyWorker got message: Hello
```

Notice that the same instance of ChattyWorker is handling messages from both Managers. The managers
ask the Worker150 program for "/user/ChattyWorker", and Worker150 looks it up in its ActorSystem and 
returns the reference to them. If you modify the Manager150 program to ask for a different worker 
(ChattyWorker2) then Worker150 will not find it and you'll see errors. Here's some example output:

```
Main M150
   akka.tcp://workerSystem@192.168.1.8:5000/user/chattyWorker2
   Did not find anything for akka.tcp://workerSystem@192.168.1.8:5000/user/chattyWorker2
```



Think about this configuration:
* Worker150 is running on the machine at 192.168.1.10
* Manager150 tries to connect using a `workerPath` containing a incorrect IP 192.168.1.50


#Lesson 200

Simple example of remote Actor creation. The Worker200 program starts first. When Manager200 starts 
it asks the Worker200 program to create a new Actor and it (Manager200) returns a reference to the
remote Actor. Manager200 then sends a "Hello Remote" message to the Worker node where it is displayed.

```scala
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
```

```scala
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
```

```
Main W200
ChattyWorker preStart
ChattyWorker got message: Hello Remote Worker
ChattyWorker preStart
ChattyWorker got message: Hello Remote Worker
```


Problems?
---------



More Information
----------------




