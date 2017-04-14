package de.admir

import scala.util.Random

case class Transaction(amount: Float = new Random().nextFloat(), timestamp: Long = System.currentTimeMillis())
