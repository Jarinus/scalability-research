package net.atos.scalability.actor

import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{ActorLogging, ActorRef, Props}
import net.atos.scalability.actor.Master._
import net.atos.scalability.analysis.{TextAnalysis, TextSubject}
import net.atos.scalability.api.API.AnalysisResponse

import scala.collection.parallel.ParSeq

class Master(workerRouter: ActorRef) extends StatefulActor[State] with ActorLogging {

  override def initialState: State = Map()

  override def receiveWithState(implicit state: State): Receive = {
    case Request(uuid, subjects, analyses, complete) =>
      val information = AnalysisInformation(
        new AtomicInteger(subjects.length),
        Nil,
        complete)

      val stateMutators: ParSeq[State => State] = subjects.par map { subject =>
        TextSubject.from(uuid, subject, analyses)
      } map handle

      val newState: State = stateMutators.foldLeft {
        state + (uuid -> information)
      } { (state, stateMutator) =>
        stateMutator(state)
      }

      log.info(s"Started '$uuid'")

      update(newState)

    case Worker.TaskResponse(subject) =>
      update(handle(subject)(state))

    case Terminate =>
      context stop self
  }

  private def handle(subject: TextSubject): State => State = state => subject.remainingAnalyses match {
    case Nil =>
      state.get(subject.analysisId) match {
        case Some(information) =>
          val newInformation = information.copy(results = information.results :+ subject)
          val newState = state + (subject.analysisId -> newInformation)

          val remaining = information.progressCounter.decrementAndGet()
          if (remaining == 0) finish(subject.analysisId)(newState)
          else newState

        case None =>
          log.warning(s"Rogue subject detected for analysis: ${subject.analysisId}")
          state
      }

    case tag :: xs =>
      val request = Worker.TaskRequest(
        tag,
        subject.copy(remainingAnalyses = xs))

      workerRouter ! request

      state
  }

  private def finish(analysisId: String): State => State = state => state.get(analysisId) match {
    case Some(information) =>
      log.info(s"Completed '$analysisId'")

      information.onComplete(
        AnalysisResponse(
          analysisId,
          information.results map { subject =>
            subject.analyzedValuesMap + ("original" -> subject.original)
          }))

      state - analysisId

    case None =>
      log.warning(s"Could not finish $analysisId: no entry found.")

      state
  }
}

object Master {

  type State = Map[String, AnalysisInformation]

  def props(workerRouter: ActorRef): Props = Props(new Master(workerRouter))

  case class AnalysisInformation(progressCounter: AtomicInteger,
                                 results: List[TextSubject],
                                 onComplete: AnalysisResponse => Unit)

  case class Request(uuid: String,
                     subjects: List[String],
                     analyses: List[TextAnalysis.Tag],
                     complete: AnalysisResponse => Unit)

  case object Terminate

}
