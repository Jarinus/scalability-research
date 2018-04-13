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

