package net.atos.scalability

import akka.NotUsed
import akka.stream.FlowShape
import akka.stream.scaladsl.{Balance, Flow, GraphDSL, Merge}
import net.atos.scalability.common.TextSubject
import net.atos.scalability.common.script.Script

object ExampleHelpers {

  implicit class ParallelFlow[R, S](flow: Flow[R, S, NotUsed]) {
    def parallel(number: Int): Flow[R, S, NotUsed] =
      Flow.fromGraph(GraphDSL.create() { implicit builder =>
        require(number > 0)

        import GraphDSL.Implicits._

        val dispatchSubject = builder.add(Balance[R](number))
        val mergeProcessedSubjects = builder.add(Merge[S](number))

        for (i <- 0 until number) {
          dispatchSubject.out(i) ~> flow.async ~> mergeProcessedSubjects.in(i)
        }

        FlowShape(dispatchSubject.in, mergeProcessedSubjects.out)
      })
  }

  implicit class _Flow(script: Script[_]) {
    lazy val flow: Flow[TextSubject, TextSubject, NotUsed] =
      Flow[TextSubject] map performAnalysis

    private def performAnalysis(subject: TextSubject): TextSubject =
      (subject.original
        |> script.analyze
        |> (analyzedValues => subject.analyzedValuesMap.put(script.tag, analyzedValues))
        |> (analyzedValuesMap => subject.copy(analyzedValuesMap = analyzedValuesMap)))
  }

}
