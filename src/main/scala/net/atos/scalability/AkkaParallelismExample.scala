package net.atos.scalability

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import net.atos.scalability.common.TextSubject
import net.atos.scalability.common.script.impl.TermFrequencyScript

import scala.concurrent.ExecutionContext

object AkkaParallelismExample extends App {
  implicit val system: ActorSystem = ActorSystem("Parallelism")
  implicit val ec: ExecutionContext = system.dispatcher

  val subjects = List.fill(8)("What is the name of the president of the United States?")

  Source(subjects)
    .map(TextSubject(_))
    .via(TermFrequencyScript.parallelFlow(4))
    .map(_.json)
    .runForeach(println)(ActorMaterializer())
    .onComplete(_ => system.terminate)
}
