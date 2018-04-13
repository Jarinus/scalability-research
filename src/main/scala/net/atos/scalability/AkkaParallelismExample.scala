package net.atos.scalability

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, FlowShape}
import akka.stream.scaladsl.{Balance, Flow, GraphDSL, Merge, Source}
import net.atos.scalability.common.TextSubject
import net.atos.scalability.common.script.impl.TermFrequencyScript

import scala.concurrent.ExecutionContext

object AkkaParallelismExample extends App {
  implicit val system: ActorSystem = ActorSystem("Parallelism")
  implicit val ec: ExecutionContext = system.dispatcher

  val subjects = List.fill(8)("What is the name of the president of the United States?")

  val flow = Flow[String] map (TextSubject(_)) via TermFrequencyScript.flow map (_.json)

  Source(subjects)
    .via(parallel(4)(flow))
    .runForeach(println)(ActorMaterializer())
    .onComplete(_ => system.terminate)

  def parallel[T, Y](number: Int)(flow: Flow[T, Y, NotUsed]): Flow[T, Y, NotUsed] =
    Flow.fromGraph(GraphDSL.create() { implicit builder =>
      require(number > 0)

      import GraphDSL.Implicits._

      val dispatchSubject = builder.add(Balance[T](number))
      val mergeProcessedSubjects = builder.add(Merge[Y](number))

      for (i <- 0 until number) {
        dispatchSubject.out(i) ~> flow.async ~> mergeProcessedSubjects.in(i)
      }

      FlowShape(dispatchSubject.in, mergeProcessedSubjects.out)
    })
}
