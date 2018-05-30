package net.atos.scalability

import akka.actor.{ActorRef, ActorSystem}
import akka.cluster.singleton.{ClusterSingletonManager, ClusterSingletonManagerSettings, ClusterSingletonProxy, ClusterSingletonProxySettings}
import akka.http.scaladsl.Http
import akka.routing.{FromConfig, RoundRobinPool}
import akka.stream.ActorMaterializer
import com.typesafe.config.{Config, ConfigFactory}
import net.atos.scalability.actor.Master.Terminate
import net.atos.scalability.actor.{Master, Worker}
import net.atos.scalability.api.API
import net.atos.scalability.utils.FunctionalProgrammingUtils.discard

import scala.concurrent.ExecutionContext

object TextAnalysisApp {

  def main(args: Array[String]): Unit = run(Configuration from args)

  def run(configuration: Configuration): Unit = {
    require(
      configuration.seedNodes.nonEmpty,
      "At least one seed node must be defined (the seed node must define itself as the seed node)")

    val (host, port) = configuration.endpoint
    val config = loadConfig(
      "akka.remote.netty.tcp.hostname" -> (if (host == "0.0.0.0") "127.0.0.1" else host),
      "akka.remote.netty.tcp.port" -> (port + 1).toString,
      "akka.cluster.seed-nodes" -> configuration.seedNodes.mkString("[\"\"\"", "\"\"\", \"\"\"", "\"\"\"]"))
      .withFallback(ConfigFactory.load)

    implicit val system: ActorSystem = ActorSystem("TextAnalysisApp", config)
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val ec: ExecutionContext = system.dispatcher

    val workerRouter = system.actorOf(
      FromConfig.props(Worker.props),
      "workerRouter")

    discard {
      system.actorOf(
        ClusterSingletonManager.props(
          singletonProps = Master.props(workerRouter),
          terminationMessage = Terminate,
          settings = ClusterSingletonManagerSettings(system)),
        name = "master")
    }

    implicit val masterRef: ActorRef = system.actorOf(
      ClusterSingletonProxy.props(
        singletonManagerPath = "/user/master",
        settings = ClusterSingletonProxySettings(system)),
      name = "masterProxy")

    discard {
      system.actorOf(
        Worker.props
          .withRouter(
            RoundRobinPool(configuration.numberOfWorkers)),
        "workers")
    }

    discard {
      Http().bindAndHandle(API.route, host, port)
    }

    println(s"Text Analysis App is listening on $host:$port-${port + 1} (locally)\n"
      + "Press any key to exit...")
  }

  private def loadConfig(keyValuePairs: (String, Any)*): Config =
    ConfigFactory.parseString(
      keyValuePairs
        .map { case (key, value) => s"$key=$value" }
        .mkString("\n"))

}
