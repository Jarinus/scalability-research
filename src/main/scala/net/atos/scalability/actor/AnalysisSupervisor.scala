package net.atos.scalability.actor

import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{Actor, ActorRef, Props}
import akka.event.Logging
import akka.routing.RoundRobinPool
import net.atos.scalability.actor.AnalysisSupervisor.Request
import net.atos.scalability.analysis.{TextAnalysis, TextSubject}

import scala.concurrent.Future

class AnalysisSupervisor(numberOfActors: Int) extends Actor {
  val log = Logging(context.system, this)
  val children: ActorRef = context.actorOf(
    AnalysisActor.props.withRouter(RoundRobinPool(numberOfActors)),
    name = "AnalysisActors")
  var progressCounters: Map[String, AtomicInteger] = Map()

  override def receive: Receive = {
    case Request(uuid, subjects, analyses) =>
      log.info(s"Starting '$uuid'")

      progressCounters += (uuid -> new AtomicInteger(subjects.length))

      subjects.par map { subject =>
        TextSubject(uuid, subject, analyses)
      } foreach onReceiveTextSubject

    case AnalysisActor.TaskResponse(subject) =>
      onReceiveTextSubject(subject)
  }

  private def onReceiveTextSubject(subject: TextSubject): Unit = subject.remainingAnalyses match {
    case Nil =>
      val progressCounter = progressCounters(subject.analysisId)
      val remaining = progressCounter.decrementAndGet()
      if (remaining == 0) onCompleted(subject)

    case x :: xs =>
      val request = AnalysisActor.TaskRequest(
        TextAnalysis.analyses(x),
        subject.copy(remainingAnalyses = xs))

      children ! request
  }

  private def onCompleted(subject: TextSubject): Unit = {
    log.info(s"Completed '${subject.analysisId}'")
    progressCounters -= subject.analysisId
  }
}

object AnalysisSupervisor {
  def props(numberOfActors: Int): Props = Props(new AnalysisSupervisor(numberOfActors))

  case class Request(uuid: String, subjects: List[String], analyses: List[TextAnalysis.Tag])

  case class Response(uuid: String, numberOfSubjects: Int)

}
