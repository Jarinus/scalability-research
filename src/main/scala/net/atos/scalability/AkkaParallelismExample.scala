package net.atos.scalability

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import net.atos.scalability.common.analysis.impl.TermFrequencyAnalysis
import net.atos.scalability.common.persistence.DataPersistor
import net.atos.scalability.common.script.Script

import scala.concurrent.ExecutionContext

object AkkaParallelismExample extends App {
  implicit val system: ActorSystem = ActorSystem("Parallelism")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val ec: ExecutionContext = system.dispatcher

  val subjects = List.fill(6000000)("What is the name of the president of the United States?")
  val parallelScript = Script {
    TermFrequencyAnalysis.flow.async
  }.initializeParallel(8)(Source(subjects), DataPersistor.ignore)

  val start = System.currentTimeMillis()

  parallelScript.run()
    .onComplete { _ =>
      println(s"6.000.000x took ${System.currentTimeMillis() - start}ms.")
      system.terminate
    }
}
