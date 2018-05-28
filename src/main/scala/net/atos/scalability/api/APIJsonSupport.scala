package net.atos.scalability.api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import net.atos.scalability.api.API.{AnalysisRequest, AnalysisResponse}
import spray.json.{DefaultJsonProtocol, JsArray, JsFalse, JsNumber, JsObject, JsString, JsTrue, JsValue, JsonFormat, RootJsonFormat}

trait APIJsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val requestFormat: RootJsonFormat[AnalysisRequest] = jsonFormat2(AnalysisRequest)

  implicit object AnalysisResponseFormat extends RootJsonFormat[AnalysisResponse] {
    override def write(response: AnalysisResponse): JsValue =
      JsObject(
        "uuid" -> JsString(response.uuid),
        "content" -> JsArray(
          response.subjects.toVector map { analyzedValues =>
            JsObject(analyzedValues map { case (key, value) =>
              (key, AnyJsonFormat write value)
            })
          })
      )

    override def read(json: JsValue): AnalysisResponse = AnalysisResponse("", Nil)
  }

  implicit object AnyJsonFormat extends JsonFormat[Any] {
    def write(a: Any): JsValue = a match {
      case n: Int => JsNumber(n)
      case s: String => JsString(s)
      case b: Boolean if b => JsTrue
      case b: Boolean if !b => JsFalse
      case l: List[_] => JsArray(l.toVector map write)
      case m: Map[_, _] => JsObject(
        m map { case (key, value) =>
          (key.toString, write(value))
        })
    }

    def read(value: JsValue): Any = value match {
      case JsNumber(n) => n.intValue()
      case JsString(s) => s
      case JsTrue => true
      case JsFalse => false
      case JsArray(elements) => elements map read
      case JsObject(fields) => fields mapValues read
    }
  }

}
