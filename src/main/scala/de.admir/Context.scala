package de.admir

import akka.actor.ActorSystem

object Context {
  implicit val actorSystem = ActorSystem("acme-actorSystem")
  implicit val ec = actorSystem.dispatcher
  val scheduler = actorSystem.scheduler
}
