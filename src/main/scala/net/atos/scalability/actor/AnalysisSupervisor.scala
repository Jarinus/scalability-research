package net.atos.scalability.actor

import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{Actor, ActorRef, Props}
import akka.event.Logging
import akka.routing.RoundRobinPool
import net.atos.scalability.actor.AnalysisSupervisor.Request
import net.atos.scalability.analysis.{TextAnalysis, TextSubject}

class AnalysisSupervisor(numberOfActors: Int) extends Actor {
  val log = Logging(context.system, this)
  val children: ActorRef = context.actorOf(
    AnalysisActor.props.withRouter(RoundRobinPool(numberOfActors)),
    name = "AnalysisActors")

  override def receive: Receive = withProgressCounters(Map())

  private def withProgressCounters(implicit progressCounters: Map[String, AtomicInteger]): Receive = {
    case Request(uuid, subjects, analyses) =>
      context.become(
        withProgressCounters(
          progressCounters + (uuid -> new AtomicInteger(subjects.length))))

      subjects.par map { subject =>
        TextSubject(uuid, subject, analyses)
      } foreach onReceiveTextSubject

      log.info(s"Started '$uuid'")

    case AnalysisActor.TaskResponse(subject) =>
      onReceiveTextSubject(subject)
  }

  private def onReceiveTextSubject(subject: TextSubject)
                                  (implicit progressCounters: Map[String, AtomicInteger])
  : Unit = subject.remainingAnalyses match {
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

  private def onCompleted(subject: TextSubject)
                         (implicit progressCounters: Map[String, AtomicInteger])
  : Unit = {
    log.info(s"Completed '${subject.analysisId}'")
    context.become(
      withProgressCounters(
        progressCounters - subject.analysisId))
  }
}

object AnalysisSupervisor {
  def props(numberOfActors: Int): Props = Props(new AnalysisSupervisor(numberOfActors))

  case class Request(uuid: String, subjects: List[String], analyses: List[TextAnalysis.Tag])

  case class Response(uuid: String, numberOfSubjects: Int)

  case class AddProgressCounter(uuid: String, count: Int)

  case class RemoveProgressCounter(uuid: String)

}
