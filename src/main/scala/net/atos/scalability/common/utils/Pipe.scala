package net.atos.scalability.common.utils

object Pipe {
  implicit class _Pipe[T](t: T) {
    def |>[Y](f: T => Y): Y = f(t)
  }
}
