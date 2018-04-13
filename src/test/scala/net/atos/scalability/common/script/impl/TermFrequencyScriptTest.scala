package net.atos.scalability.common.script.impl

import org.scalatest.{FunSuite, Matchers}

class TermFrequencyScriptTest extends FunSuite with Matchers {
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
    TermFrequencyScript.analyze(testString).toSet should equal (expectedTermFrequency.toSet)
  }

}
