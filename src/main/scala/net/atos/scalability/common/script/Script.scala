package net.atos.scalability.common.script

import akka.stream.FlowShape
import akka.stream.scaladsl.{Balance, Flow, GraphDSL, Keep, Merge, RunnableGraph, Source}
import akka.{Done, NotUsed}
import net.atos.scalability.common.TextSubject
import net.atos.scalability.common.persistence.DataPersistor

import scala.concurrent.Future

case class Script(analyses: Flow[TextSubject, TextSubject, NotUsed]) {

  final def initialize(source: Source[String, NotUsed], persistor: DataPersistor): RunnableGraph[Future[Done]] =
    combine(source, analyses, persistor)

  final def initializeParallel(cores: Int)(source: Source[String, NotUsed], persistor: DataPersistor): RunnableGraph[Future[Done]] =
    combine(source, parallelFlow(4), persistor)

  private def combine(source: Source[String, NotUsed], flow: Flow[TextSubject, TextSubject, NotUsed], persistor: DataPersistor): RunnableGraph[Future[Done]] =
    prepare(source).via(flow).toMat(persistor.sink)(Keep.right)

  private def prepare(source: Source[String, NotUsed]): Source[TextSubject, NotUsed] = source.map(TextSubject(_))

  private def parallelFlow(cores: Int): Flow[TextSubject, TextSubject, NotUsed] =
    Flow.fromGraph(GraphDSL.create() { implicit builder =>
      import GraphDSL.Implicits._

      val dispatchSubjects = builder.add(Balance[TextSubject](cores))
      val mergeSubjects = builder.add(Merge[TextSubject](cores))

      for (i <- 0 until cores) {
        dispatchSubjects.out(i) ~> analyses.async ~> mergeSubjects.in(i)
      }

      FlowShape(dispatchSubjects.in, mergeSubjects.out)
    })

}
