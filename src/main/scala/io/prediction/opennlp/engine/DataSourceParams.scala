package io.prediction.opennlp.engine

import org.apache.predictionio.controller.Params

case class DataSourceParams(
  appName: String,
  eventNames: List[String],
  cutoff: Int) extends Params
