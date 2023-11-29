ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.1"

resolvers += "Akka library repository".at("https://repo.akka.io/maven")
val AkkaVersion = "2.9.0"
val AkkaHttpVersion = "10.6.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-serialization-jackson" % AkkaVersion
)

lazy val root = (project in file("."))
  .settings(
    name := "backend"
  )
