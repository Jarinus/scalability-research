package net.atos.scalability.utils

object FunctionalProgrammingUtils {
  def discard(f: Any): Unit = {
    val _ = f
    ()
  }
}
