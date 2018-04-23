package net.atos.scalability.common.persistence

import akka.Done
import akka.stream.scaladsl.Sink
import net.atos.scalability.common.TextSubject

import scala.concurrent.Future

trait DataPersistor {

  lazy val sink: Sink[TextSubject, Future[Done]] =
    Sink foreach handle

  def handle(subject: TextSubject): Unit

}

object DataPersistor {
  lazy val ignore: DataPersistor = _ => ()
  lazy val print: DataPersistor = subject => println(subject.json)
}
