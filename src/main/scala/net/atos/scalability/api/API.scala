package net.atos.scalability.api

import java.util.UUID

import akka.actor.ActorRef
import akka.http.scaladsl.marshalling.ToResponseMarshaller
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import net.atos.scalability.actor.Master.Request

object API extends APIJsonSupport {
  def route(implicit master: ActorRef): Route =
    path("analysis") {
      post {
        entity(as[AnalysisRequest]) { request =>
          val uuid = UUID.randomUUID.toString

          completeWith(implicitly[ToResponseMarshaller[AnalysisResponse]]) { complete =>
            master ! Request(uuid, request.subjects, request.analyses, complete)
          }
        }
      }
    }

  final case class AnalysisRequest(subjects: List[String], analyses: List[String])

  final case class AnalysisResponse(uuid: String, subjects: List[Map[String, Any]])

}
