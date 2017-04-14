package de.admir

import Context._
import de.admir.TransactionsStatisticActor.Messages.{Add, GetStatistic}
import akka.pattern.ask

import scala.concurrent.Await
import scala.concurrent.duration._


case class TransactionsStatisticMeter(duration: FiniteDuration) {
  private val transactionsStatisticActorRef =
    actorSystem.actorOf(TransactionsStatisticActor.props(duration), "transactions-statistic-actor")

  def mark(transaction: Transaction): Unit = {
    transactionsStatisticActorRef ! Add(transaction)
  }

  def statistic(): Option[TransactionStatistic] = {
    val statisticFuture = (transactionsStatisticActorRef ? GetStatistic)(5.seconds)
      .mapTo[Option[TransactionStatistic]]
    Await.result(statisticFuture, 5.seconds)
  }
}
