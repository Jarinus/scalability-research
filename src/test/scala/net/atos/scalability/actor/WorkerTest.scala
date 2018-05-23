package net.atos.scalability.actor

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import net.atos.scalability.actor.Worker.{TaskRequest, TaskResponse}
import net.atos.scalability.analysis.impl.TermFrequencyAnalysis
import net.atos.scalability.analysis.{TextAnalysis, TextSubject}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class WorkerTest extends TestKit(ActorSystem("AnalysisActorTest")) with ImplicitSender with WordSpecLike
  with Matchers with BeforeAndAfterAll {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "An analysis actor" must {

    "send back a response with the analysis added" in {
      val analysisActor = system.actorOf(Worker.props)
      val subject = TextSubject.from("id", "Test", Nil)

      analysisActor ! TaskRequest(TermFrequencyAnalysis.tag, subject)

      expectMsg(TaskResponse(TermFrequencyAnalysis perform subject))
    }

  }

}
