package net.atos.scalability.common.analysis.impl

import net.atos.scalability.common.analysis.TextAnalysis

object TermFrequencyAnalysis extends TextAnalysis[Map[String, Int]]("term-frequency") {

  override def analyze(subject: String): Map[String, Int] =
    omitPunctuationMarks(subject)
      .toLowerCase
      .split(" ")
      .groupBy(identity)
      .mapValues(_.length)

  private def omitPunctuationMarks(s: String): String =
    s replaceAll("[^a-zA-Z\\d\\s]", "")
}
