package net.atos.scalability.actor

import akka.actor.{Actor, Props}
import net.atos.scalability.actor.AnalysisActor.{TaskRequest, TaskResponse}
import net.atos.scalability.analysis.{TextAnalysis, TextSubject}

class AnalysisActor extends Actor {
  override def receive: Receive = {
    case TaskRequest(analysis, subject) =>
      val newSubject = analysis perform subject
      sender ! TaskResponse(newSubject)
  }
}

object AnalysisActor {
  def props: Props = Props(new AnalysisActor())

  case class TaskRequest(analysis: TextAnalysis[_], subject: TextSubject)

  case class TaskResponse(subject: TextSubject)

}
