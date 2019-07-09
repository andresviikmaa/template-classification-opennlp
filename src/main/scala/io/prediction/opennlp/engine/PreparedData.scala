package io.prediction.opennlp.engine

import opennlp.tools.ml.model.DataIndexer

case class PreparedData(dataIndexer: DataIndexer) extends Serializable
