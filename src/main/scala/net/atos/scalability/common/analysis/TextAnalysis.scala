package net.atos.scalability.common.analysis

import akka.NotUsed
import akka.stream.scaladsl.Flow
import net.atos.scalability.common.TextSubject
import net.atos.scalability.common.analysis.TextAnalysis.Tag

abstract class TextAnalysis[T](val tag: Tag) {
  protected def analyze(subject: String): T

  lazy val flow: Flow[TextSubject, TextSubject, NotUsed] = Flow[TextSubject] map performAnalysis

  private def performAnalysis(subject: TextSubject): TextSubject =
    subject.copy(analyzedValuesMap = subject.analyzedValuesMap + (tag -> analyze(subject.original)))
}

object TextAnalysis {
  type Tag = String
}

