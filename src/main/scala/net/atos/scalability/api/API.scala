package net.atos.scalability.api

import java.util.UUID

import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import net.atos.scalability.actor.AnalysisSupervisor

object API extends APIJsonSupport {
  def route(implicit supervisor: ActorRef): Route =
    path("analysis") {
      post {
        entity(as[AnalysisRequest]) { request =>
          val uuid = UUID.randomUUID().toString

          supervisor ! AnalysisSupervisor.Request(uuid, request.subjects, request.analyses)

          complete(AnalysisResponse(uuid))
        }
      }
    }


  final case class AnalysisRequest(subjects: List[String], analyses: List[String])

  final case class AnalysisResponse(uuid: String)

}
