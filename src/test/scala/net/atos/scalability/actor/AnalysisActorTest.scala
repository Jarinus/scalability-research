package net.atos.scalability.actor

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import net.atos.scalability.actor.AnalysisActor.{TaskRequest, TaskResponse}
import net.atos.scalability.analysis.{TextAnalysis, TextSubject}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class AnalysisActorTest extends TestKit(ActorSystem("AnalysisActorTest")) with ImplicitSender with WordSpecLike
  with Matchers with BeforeAndAfterAll {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "An analysis actor" must {

    "send back a response with the analysis added" in {
      val analysisActor = system.actorOf(AnalysisActor.props)
      val subject = TextSubject("id", "Test", Nil)
      val analysis = new TextAnalysis[String]("test") {
        override protected def analyze(subject: String): String = subject
      }

      analysisActor ! TaskRequest(analysis, subject)

      expectMsg(TaskResponse(subject.copy(
        analyzedValuesMap = Map("test" -> subject.original)
      )))
    }

  }

}
