package net.atos.scalability.analysis

case class TextSubject private(analysisId: String,
                               original: String,
                               remainingAnalyses: List[TextAnalysis.Tag],
                               analyzedValuesMap: Map[TextAnalysis.Tag, Any] = Map())
