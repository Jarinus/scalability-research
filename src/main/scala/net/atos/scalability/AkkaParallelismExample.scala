package net.atos.scalability

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Source}
import net.atos.scalability.ExampleHelpers._
import net.atos.scalability.common.TextSubject
import net.atos.scalability.common.script.impl.TermFrequencyScript

import scala.concurrent.ExecutionContext

object AkkaParallelismExample extends App {
  implicit val system: ActorSystem = ActorSystem("Parallelism")
  implicit val ec: ExecutionContext = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val subjects = List.fill(8)("What is the name of the president of the United States?")
  val flow = (Flow[String] map (TextSubject(_)))
    .via(TermFrequencyScript.flow)
    .map(_.json)

  Source(subjects)
    .via(flow.parallel(4))
    .runForeach(println)
    .onComplete(_ => system.terminate)
}
