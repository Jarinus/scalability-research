package net.atos.scalability

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import net.atos.scalability.ExampleHelpers._
import net.atos.scalability.common.TextSubject
import net.atos.scalability.common.script.impl.TermFrequencyScript

object AkkaConcurrencyExample extends App {
  implicit val system: ActorSystem = ActorSystem("Concurrency")

  Source.single("What is the name of the president of the United States?")
    .map(TextSubject(_))
    .via(TermFrequencyScript.flow)
    .map(_.json)
    .runForeach(println)(ActorMaterializer())

  system.terminate
}
