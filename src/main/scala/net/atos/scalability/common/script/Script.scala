package net.atos.scalability.common.script

import net.atos.scalability.common.script.Script.Tag

abstract class Script[T](val tag: Tag) {
  def analyze(subject: String): T
}

object Script {
  type Tag = String
}

