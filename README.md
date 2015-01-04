Akka Remote Tutorial
======================

This is a tutorial about using Akka remote actors. I hope that by taking a different approach from 
 existing tutorials that I can create something helpful.
 
 I assume you know Scala, SBT, and the basics of Akka. If so then we're both on the same page. I am
 not an expert, just trying to learn this stuff as are you.
 
Location Transparency and Environment
-------------------------------------

The Akka people have worked hard to make Akka remoting as simple as possible. For the most part very
little of your code will need to change when you covert over. That is the good news. The bad news is
that how you think about your code and design may change, and that may lead to code changes. This should
not be surprising...

It is possible to run multiple remote actors in a single JVM and many of the examples on the web do exactly
that. I think that is confusing as hell and I'm not taking that approach. You will need to open two
terminal windows and invoke `sbt` from them. Some times you may want to run the examples on separate 
machines and in this case you have two options: run `sbt` on both machines, or copy the uber jar and run
it directly. In my case my other machine is a Raspberry Pi and invoking `sbt` on it is painful.

Stupid Stuff
------------

In my examples I have actors for Managers and Workers. I picked these roles and names because I don't 
like names like "ActorA" or "ActorSystem3".

The example code files are given names like "Worker150" and "Manager200". Typically Workers and Managers
with the same numeric ending are meant to work together. So "Worker340" and "Manager340" go together. The
numbers don't mean anything.

Running the Code
----------------

I am using Scala 2.11.4, Akka 2.3.4, and SBT 0.13.7

Follow these steps to run the code:

1. Open two terminals. I'll refer to them as left and right.
2. In the left terminal `cd` to the _AkkaRemotingTutorial_ directory.
3. Type `sbt 'run W150'`. On my desktop I get this output:
<pre>
> sbt 'run W150'
[info] Loading project definition from /home/chris/projects/akka/AkkaRemotingTutorial/project
[info] Set current project to Tutorial (in build file:/home/chris/projects/akka/AkkaRemotingTutorial/)
[info] Running org.bodhi.remoting.tutorial.Main W150
Main W150
ChattyWorker preStart
</pre>
4. In the right teminal `cd` to the _AkkaRemotingTutorial_ directory.
5. Type `sbt 'run M150'`. On my desktop I get this output:
<pre>
> sbt 'run M150'
[info] Loading project definition from /home/chris/projects/akka/AkkaRemotingTutorial/project
[info] Set current project to Tutorial (in build file:/home/chris/projects/akka/AkkaRemotingTutorial/)
[info] Running org.bodhi.remoting.tutorial.Main M150
Main M150
Looking for akka.tcp://workerSystem@127.0.0.1:5000/user/chattyWorker
found: Some(Actor[akka.tcp://workerSystem@127.0.0.1:5000/user/chattyWorker#537855532])
</pre>


The two consoles should ... When the action stops, stop each system by pressing
Ctrl-C.


Problems?
---------

If you're having any problems with this code, edit the _application.conf_
file in the _src/main/resources_ directory of each project, and remove the
comments from the debug-related lines.

More Information
----------------




