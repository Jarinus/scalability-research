package net.atos.scalability.api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import net.atos.scalability.api.API.{AnalysisRequest, AnalysisResponse}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait APIJsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val requestFormat: RootJsonFormat[AnalysisRequest] = jsonFormat2(AnalysisRequest)
  implicit val responseFormat: RootJsonFormat[AnalysisResponse] = jsonFormat1(AnalysisResponse)
}
