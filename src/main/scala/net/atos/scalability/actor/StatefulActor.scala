package net.atos.scalability.actor

import akka.actor.Actor

abstract class StatefulActor[T] extends Actor {

  def initialState: T

  def receiveWithState(implicit t: T): Receive

  protected def update(t: T): Unit = context.become(receiveWithState(t))

  override def receive: Receive = receiveWithState(initialState)

}
