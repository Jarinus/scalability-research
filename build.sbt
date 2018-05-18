import com.typesafe.sbt.packager.docker.Cmd

name := "jarinus/text-analysis-app"
mainClass in Compile := Some("net.atos.scalability.TextAnalysisApp")

version := "0.1"
scalaVersion := "2.12.5"
val akkaVersion = "2.5.12"
val akkaHttpVersion = "10.1.1"

/*
 * Docker
 */
enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)
enablePlugins(AshScriptPlugin)

dockerBaseImage := "openjdk:jre-alpine"
packageName in Docker := "text-analysis-app"

dockerUsername := Some("jarinus")
dockerUpdateLatest := true

//dockerBuildOptions ++= List(
//  "-t",
//  dockerAlias.value.copy(tag = Some("latest")).versioned)

dockerCommands ++= Seq(
  Cmd("ENV", "port=8080"),
  Cmd("EXPOSE", "$port"))

/*
 * Dependencies
 */
libraryDependencies ++= Seq( // Akka
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion
)

libraryDependencies ++= Seq( // Scala Test
  "org.scalactic" %% "scalactic" % "3.0.5",
  "org.scalatest" %% "scalatest" % "3.0.5" % Test
)

libraryDependencies ++= Seq( // Logback
  "ch.qos.logback" % "logback-classic" % "1.2.3"
)
