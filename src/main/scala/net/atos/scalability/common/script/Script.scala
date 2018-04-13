package net.atos.scalability.common.script

import akka.NotUsed
import akka.stream.FlowShape
import akka.stream.scaladsl.{Balance, Flow, GraphDSL, Merge}
import net.atos.scalability.common.TextSubject
import net.atos.scalability.common.script.Script.Tag
import net.atos.scalability.common.utils.MapUtils._
import net.atos.scalability.common.utils.Pipe._

abstract class Script[T](val tag: Tag) {
  lazy val flow: Flow[TextSubject, TextSubject, NotUsed] =
    Flow[TextSubject] map performAnalysis

  def parallelFlow(number: Int): Flow[TextSubject, TextSubject, NotUsed] =
    Flow.fromGraph(GraphDSL.create() { implicit builder =>
      require(number > 0)

      import GraphDSL.Implicits._

      val dispatchSubject = builder.add(Balance[TextSubject](number))
      val mergeProcessedSubjects = builder.add(Merge[TextSubject](number))

      for (i <- 0 until number) {
        dispatchSubject.out(i) ~> flow.async ~> mergeProcessedSubjects.in(i)
      }

      FlowShape(dispatchSubject.in, mergeProcessedSubjects.out)
    })

  def analyze(subject: String): T

  private def performAnalysis(subject: TextSubject): TextSubject =
    (subject.original
      |> analyze
      |> (analyzedValues => subject.analyzedValuesMap.put(tag, analyzedValues))
      |> (analyzedValuesMap => subject.copy(analyzedValuesMap = analyzedValuesMap)))
}

object Script {
  type Tag = String
}

