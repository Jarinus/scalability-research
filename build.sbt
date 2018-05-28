import com.typesafe.sbt.packager.docker.Cmd

// Application information
name := "jarinus/text-analysis-app"
mainClass in Compile := Some("net.atos.scalability.TextAnalysisApp")

// Versions
version := "0.2"
scalaVersion := "2.12.6"
val akkaVersion = "2.5.12"
val akkaHttpVersion = "10.1.1"
val scalaTestVersion = "3.0.5"
val logbackVersion = "1.2.3"

// Code quality
wartremoverErrors ++= Warts.unsafe
wartremoverErrors -= Wart.Any // Akka uses the Any type for actor messaging

// Docker
packageName in Docker := "text-analysis-app"
dockerBaseImage := "openjdk:jre-alpine"
dockerUsername := Some("jarinus")
dockerUpdateLatest := true

dockerCommands ++= Seq(
  Cmd("ENV", "port=8080"),
  Cmd("EXPOSE", "$port"))

// Dependencies
libraryDependencies ++= Seq(
  // Akka
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,

  // Scala Test
  "org.scalactic" %% "scalactic" % scalaTestVersion,
  "org.scalatest" %% "scalatest" % scalaTestVersion % Test,

  // JSON4S for recursive JSON (required for TextSubject's Map[String, Any])
  "org.json4s" %% "json4s-native" % "3.6.0-M3",

  // Logback
  "ch.qos.logback" % "logback-classic" % logbackVersion)

// Plugins
enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)
enablePlugins(AshScriptPlugin)
