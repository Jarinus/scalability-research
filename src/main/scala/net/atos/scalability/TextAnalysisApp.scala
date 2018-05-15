package net.atos.scalability

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import net.atos.scalability.actor.AnalysisSupervisor
import net.atos.scalability.api.API

import scala.concurrent.ExecutionContext
import scala.io.StdIn

object TextAnalysisApp {
  implicit val system: ActorSystem = ActorSystem("TextAnalysisApp")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val ec: ExecutionContext = system.dispatcher

  def main(args: Array[String]): Unit = run(Configuration from args)

  private def run(configuration: Configuration): Unit = {
    implicit val supervisor: ActorRef = system.actorOf(AnalysisSupervisor.props(configuration.numberOfWorkers))
    val (host, port) = configuration.endpoint

    val bindingFuture = Http().bindAndHandle(API.route, host, port)

    println(s"Server online at http://$host:$port/\nPress RETURN to stop...")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }

}
