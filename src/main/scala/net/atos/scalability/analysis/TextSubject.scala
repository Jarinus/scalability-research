package net.atos.scalability.analysis

import org.json4s.NoTypeHints
import org.json4s.native.Serialization
import org.json4s.native.Serialization._

case class TextSubject private(analysisId: String,
                               original: String,
                               remainingAnalyses: List[TextAnalysis.Tag],
                               analyzedValuesMap: Map[TextAnalysis.Tag, Any] = Map()) {
  lazy val json: String =
    write(analyzedValuesMap + ("original" -> original))(Serialization.formats(NoTypeHints))
}
