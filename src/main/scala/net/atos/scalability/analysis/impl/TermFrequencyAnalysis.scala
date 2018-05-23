package net.atos.scalability.analysis.impl

import net.atos.scalability.analysis.TextAnalysis

object TermFrequencyAnalysis extends TextAnalysis[Map[String, Int]]("term-frequency") {

  override def analyze(subject: String): Map[String, Int] =
    omitPunctuationMarks(subject)
      .toLowerCase
      .split(" ")
      .groupBy(identity)
      .mapValues(_.length)
      .map(identity)

  private def omitPunctuationMarks(s: String): String =
    s replaceAll("[^a-zA-Z\\d\\s]", "")
}
