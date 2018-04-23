package net.atos.scalability

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import net.atos.scalability.common.analysis.impl.TermFrequencyAnalysis
import net.atos.scalability.common.persistence.DataPersistor
import net.atos.scalability.common.script.Script

import scala.concurrent.ExecutionContext

object AkkaConcurrencyExample extends App {
  implicit val system: ActorSystem = ActorSystem("Concurrency")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val ec: ExecutionContext = system.dispatcher

  val subjects = List.fill(6000000)("What is the name of the president of the United States?")
  val concurrentScript = Script {
    TermFrequencyAnalysis.flow
  }.initialize(Source(subjects), DataPersistor.ignore)

  val start = System.currentTimeMillis()

  concurrentScript.run()
    .onComplete { _ =>
      println(s"6.000.000x took ${System.currentTimeMillis() - start}ms.")
      system.terminate
    }
}
