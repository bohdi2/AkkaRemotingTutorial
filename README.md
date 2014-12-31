Akka Remote Tutorial 0
======================

This is a tutorial about using Akka remote actors. I start with
a simple example taken from the "Let It Crash" website: http://letitcrash.com/post/14630948149/location-transparency-remoting-in-akka-2-0
I have updated the example to Akka 2.3.4 and placed it under the org.bodhi package.

In this first example there are two actors running under the same JVM. 

As it's name indicates, this is a simple "Hello, world" example
for Akka remote actors. It shows how to create a local actor,
a remote actor, and send messages between them.

This is a fork of Alvin Alexander's original code. I am modifying it to
learn about creating distributions with STB. My goal is to deploy the
remote piece on RaspBerry Pis.

Assumptions
-----------

For the purposes of this code, I assume you know the following:

1. Scala
1. SBT (the Simple Build Tool)
1. How to use Akka actors within one JVM (i.e., the actor basics)

Running the Code
----------------

Follow these steps to run the code:

1. `cd` into the _HelloRemote_ directory.
1. Type `sbt run` to start the remote actor system.
1. In a separate terminal window, `cd` into the _HelloLocal_ directory.
1. Type `sbt run` to start the local actor system.

When the local actor system starts, it will send an initial message
to the remote actor system. The remote actor will send a reply through
its `sender` reference, and this will continue five times. When the
action stops, stop each system by pressing Ctrl-C.

Problems?
---------

If you're having any problems with this code, edit the _application.conf_
file in the _src/main/resources_ directory of each project, and remove the
comments from the debug-related lines.

More Information
----------------

See the following URL for more and original information:

http://alvinalexander.com/scala/simple-akka-actors-remote-example


