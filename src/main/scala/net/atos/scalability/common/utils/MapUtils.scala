package net.atos.scalability.common.utils

object MapUtils {
  implicit class _Map[K, V](map: Map[K, V]) {
    def put(k: K, v: V): Map[K, V] =
      map + (k -> v)
  }
}
