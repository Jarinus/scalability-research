package net.atos.scalability.common

import net.atos.scalability.common.script.Script
import net.atos.scalability.common.utils.MapUtils._
import org.json4s.NoTypeHints
import org.json4s.native.Serialization
import org.json4s.native.Serialization._

case class TextSubject(original: String,
                       analyzedValuesMap: Map[Script.Tag, Any] = Map()) {
  lazy val json: String =
    write(analyzedValuesMap.put("original", original))(Serialization.formats(NoTypeHints))
}
