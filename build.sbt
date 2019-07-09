

name := "pio-corenlp"

val pioVersion = "0.14.0"

organization := "org.apache.predictionio"
scalaVersion := "2.11.11"
scalaVersion in ThisBuild := "2.11.11"
val sparkVersion = "2.1.1"

libraryDependencies ++= Seq(
  "org.apache.predictionio" %% "apache-predictionio-core" % pioVersion % "provided",
  "org.apache.spark" %% "spark-core" % "2.1.1" % "provided",
  "org.apache.spark" %% "spark-mllib" % "2.1.1" % "provided",
  "org.apache.opennlp" % "opennlp-maxent" % "3.0.3",
  "edu.stanford.nlp" % "stanford-corenlp" % "3.5.1",
  "org.scalatest" %% "scalatest" % "2.1.3" % Test)
