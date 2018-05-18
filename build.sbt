import com.typesafe.sbt.packager.docker.Cmd

name := "text-analysis-app"
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

dockerBuildOptions ++= List(
  "-t",
  dockerAlias.value.copy(tag = Some("latest")).versioned)

dockerCommands ++= Seq(
  Cmd("ENV", "port=8080"),
  Cmd("EXPOSE", "$port"))

dockerBaseImage := "openjdk:jre-alpine"
packageName in Docker := "text-analysis-app"
/*
 * Dependencies
 */
// Akka
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion
)

// Scala Test
libraryDependencies ++= Seq(
  "org.scalactic" %% "scalactic" % "3.0.5",
  "org.scalatest" %% "scalatest" % "3.0.5" % Test
)

// Logback
libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.2.3"
)
