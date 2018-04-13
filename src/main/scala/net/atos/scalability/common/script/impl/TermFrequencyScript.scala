package net.atos.scalability.common.script.impl

import net.atos.scalability.common.script.Script
import net.atos.scalability.common.utils.Pipe._

object TermFrequencyScript extends Script[Map[String, Int]]("term-frequency") {

  override def analyze(subject: String): Map[String, Int] =
    (omitPunctuationMarks(subject)
      |> (_.toLowerCase)
      |> (_.split(" "))
      |> (_.groupBy(identity))
      |> (_.mapValues(_.length)))

  private def omitPunctuationMarks(s: String): String =
    s replaceAll("[^a-zA-Z\\d\\s]", "")
}
