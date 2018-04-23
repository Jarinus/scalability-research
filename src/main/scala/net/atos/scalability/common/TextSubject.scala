package net.atos.scalability.common

import net.atos.scalability.common.analysis.TextAnalysis
import org.json4s.NoTypeHints
import org.json4s.native.Serialization
import org.json4s.native.Serialization._

case class TextSubject(original: String,
                       analyzedValuesMap: Map[TextAnalysis.Tag, Any] = Map()) {
  lazy val json: String =
    write(analyzedValuesMap + ("original" -> original))(Serialization.formats(NoTypeHints))
}
