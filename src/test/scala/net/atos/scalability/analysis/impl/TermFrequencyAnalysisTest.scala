package net.atos.scalability.analysis.impl

import org.scalatest.{Matchers, WordSpec, WordSpecLike}

class TermFrequencyAnalysisTest extends WordSpec with Matchers{
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

  "A term frequency analysis must" must {
    val analyzed = TermFrequencyAnalysis.analyze(testString)

    "be case insensitive" in {
      analyzed forall { case (word, _) =>
          word forall (_.isLower)
      } shouldBe true
    }

    "completely match the expected set" in {
      analyzed.toSet should equal (expectedTermFrequency.toSet)
    }

  }

}
