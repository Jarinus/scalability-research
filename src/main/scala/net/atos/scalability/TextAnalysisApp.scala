package net.atos.scalability

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{RunnableGraph, Source}
import akka.{Done, NotUsed}
import net.atos.scalability.common.persistence.DataPersistor
import net.atos.scalability.common.script.impl.DefaultScript

import scala.concurrent.{ExecutionContext, Future}

object TextAnalysisApp {

  def main(args: Array[String]): Unit = run(Configuration initialize args)

  private def run(configuration: Configuration): Unit = {
    implicit val system: ActorSystem = ActorSystem("TextAnalysisApp")
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val ec: ExecutionContext = system.dispatcher

    val initialize: (Source[String, NotUsed], DataPersistor) => RunnableGraph[Future[Done]] =
      if (configuration.numberOfThreads == 1) DefaultScript.initialize
      else DefaultScript.initializeParallel(configuration.numberOfThreads)

    initialize(Source.empty, DataPersistor.ignore)
      .run()
      .onComplete(_ => {
        println(s"Successfully ran with ${configuration.numberOfThreads} thread(s)")
        system.terminate
      })
  }

}
