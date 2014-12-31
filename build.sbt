
//import com.typesafe.sbt.packager.Keys._
import com.typesafe.sbt.SbtNativePackager._
//import NativePackagerKeys._

packageArchetype.java_application

maintainer := "Your Name <your@email.com>"

//maintainer in Debian := "Your Name <your@email.com>"

organization := "org.bodhi"

name := "Tutorial"

version := "1.0"

scalaVersion := "2.11.4"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.4",
  "com.typesafe.akka" %% "akka-remote" % "2.3.4"
)



