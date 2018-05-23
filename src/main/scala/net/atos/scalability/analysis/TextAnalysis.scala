package net.atos.scalability.analysis

import net.atos.scalability.analysis.TextAnalysis.Tag
import net.atos.scalability.analysis.impl.TermFrequencyAnalysis

abstract class TextAnalysis[T](val tag: Tag) {
  def perform(subject: TextSubject): TextSubject =
    subject.copy(analyzedValuesMap = subject.analyzedValuesMap + (tag -> analyze(subject.original)))

  protected def analyze(subject: String): T
}

object TextAnalysis {
  type Tag = String

  lazy val analyses: Map[Tag, TextAnalysis[_]] = toMap(
    TermFrequencyAnalysis
  )

  private def toMap(analyses: TextAnalysis[_]*): Map[Tag, TextAnalysis[_]] =
    (analyses map (analysis => analysis.tag -> analysis)).toMap
}

