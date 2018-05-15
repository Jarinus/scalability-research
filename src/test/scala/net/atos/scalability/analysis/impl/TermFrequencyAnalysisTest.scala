package net.atos.scalability.analysis.impl

import org.scalatest.{FunSuite, Matchers}

class TermFrequencyAnalysisTest extends FunSuite with Matchers {
  val testString = "What is the name of the president of the United States?"
  val expectedTermFrequency = Map(
    "what" -> 1,
    "is" -> 1,
    "the" -> 3,
    "name" -> 1,
    "of" -> 2,
    "president" -> 1,
    "united" -> 1,
    "states" -> 1
  )

  test("term frequency") {
    TermFrequencyAnalysis.analyze(testString).toSet should equal (expectedTermFrequency.toSet)
  }

}
