package de.admir

import Context._
import sext._

import scala.concurrent.duration._

object Main extends App {

  /*
   * Create a meter which tracks transactions of the last 20 s
   */
  val transactionsStatisticMeter = TransactionsStatisticMeter(20.seconds)

  /*
   * Simulate the arrival of a new transaction every 100 ms
   */
  val markJob = scheduler.schedule(0.seconds, 100.milliseconds) {
    transactionsStatisticMeter.mark(Transaction())
  }

  /*
   * Get and print the statistic every 5 s
   */
  val getAndPrintJob = scheduler.schedule(0.seconds, 5.seconds) {
    val transactionsStatistic = transactionsStatisticMeter.statistic()
    transactionsStatistic.foreach(statistic => println(s"TransactionsStatistic: \n${statistic.valueTreeString}\n"))
  }

  /*
   * Graceful stop of the schedulers on JVM shutdown
   */
  Runtime.getRuntime.addShutdownHook(new Thread() {
    override def run(): Unit = {
      markJob.cancel()
      getAndPrintJob.cancel()
    }
  })
}
