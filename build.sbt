import scala.language.postfixOps

name := "transactions-statistic"

version := "1.0"

scalaVersion := "2.12.1"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.0-RC2" withSources,
  "com.github.nikita-volkov" % "sext" % "0.2.4" withSources
)
