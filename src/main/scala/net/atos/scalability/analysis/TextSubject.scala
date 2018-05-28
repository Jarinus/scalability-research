package net.atos.scalability.analysis

case class TextSubject(analysisId: String,
                       original: String,
                       remainingAnalyses: List[TextAnalysis.Tag],
                       analyzedValuesMap: Map[TextAnalysis.Tag, Any])

object TextSubject {
  def from(analysisId: String, original: String, remainingAnalyses: List[TextAnalysis.Tag]): TextSubject =
    TextSubject(analysisId, original, remainingAnalyses, Map())
}
