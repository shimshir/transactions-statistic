package de.admir

import Context.ec
import akka.actor.{Actor, Props}
import scala.concurrent.duration._

class TransactionsStatisticActor(windowWidth: FiniteDuration, timeProvider: () => Long) extends Actor {

  import TransactionsStatisticActor.Messages._

  var transactionsWindow: Seq[Transaction] = Nil
  var transactionsStatistic: Option[TransactionStatistic] = None

  context.system.scheduler.schedule(0.seconds, 1.nanosecond, self, UpdateStatistic)

  def receive = {
    case Add(transaction) =>
      transactionsWindow = transaction +: transactionsWindow

    case GetStatistic =>
      sender() ! transactionsStatistic

    case UpdateStatistic =>
      transactionsWindow = trimWindow(transactionsWindow)
      transactionsStatistic = extractStatistic(transactionsWindow)

    case msg =>
      println(s"Received unknown message: $msg")
  }

  private def trimWindow(transactionWindow: Seq[Transaction]): Seq[Transaction] =
    transactionWindow.filter(transaction => transaction.timestamp > timeProvider() - windowWidth.toMillis)

  private def extractStatistic(transactionWindow: Seq[Transaction]): Option[TransactionStatistic] = {
    transactionWindow match {
      case Nil => None
      case nonEmptyWindow =>
        val sum = nonEmptyWindow.map(_.amount).sum
        val count = nonEmptyWindow.size
        val avg = sum / count
        val min = nonEmptyWindow.map(_.amount).min
        val max = nonEmptyWindow.map(_.amount).max
        Some(TransactionStatistic(sum, avg, min, max, count))
    }
  }
}

object TransactionsStatisticActor {

  object Messages {
    case class Add(transaction: Transaction)
    object GetStatistic
    private[TransactionsStatisticActor] object UpdateStatistic

  }

  def props[Transaction](windowWidth: FiniteDuration, timeProvider: () => Long = () => System.currentTimeMillis()): Props =
    Props(new TransactionsStatisticActor(windowWidth, timeProvider))
}
