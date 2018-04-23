package net.atos.scalability.common.script.impl

import net.atos.scalability.common.analysis.impl.TermFrequencyAnalysis
import net.atos.scalability.common.script.Script

object DefaultScript extends Script(
  TermFrequencyAnalysis.flow.async
)
