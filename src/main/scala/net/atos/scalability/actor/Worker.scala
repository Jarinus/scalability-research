package net.atos.scalability.actor

import akka.actor.{Actor, Props}
import net.atos.scalability.actor.Worker.{TaskRequest, TaskResponse}
import net.atos.scalability.analysis.{TextAnalysis, TextSubject}

class Worker extends Actor {
  override def receive: Receive = {
    case TaskRequest(tag, subject) =>
      val analysis = TextAnalysis.analyses(tag)
      val newSubject = analysis perform subject
      sender ! TaskResponse(newSubject)
  }
}

object Worker {
  def props: Props = Props(new Worker())

  case class TaskRequest(tag: TextAnalysis.Tag, subject: TextSubject)

  case class TaskResponse(subject: TextSubject)

}
