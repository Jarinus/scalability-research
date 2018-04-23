name := "text-analysis-app"

version := "0.1"

scalaVersion := "2.12.5"

// Akka Stream
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % "2.5.12",
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.12" % Test
)

// JSON
libraryDependencies += "org.json4s" %% "json4s-native"% "3.6.0-M3"

// Scala Test
libraryDependencies ++= Seq(
  "org.scalactic" %% "scalactic" % "3.0.5",
  "org.scalatest" %% "scalatest" % "3.0.5" % Test
)
