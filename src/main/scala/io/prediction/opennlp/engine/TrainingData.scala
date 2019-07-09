package io.prediction.opennlp.engine

import opennlp.tools.ml.model.DataIndexer

case class TrainingData(dataIndexer: DataIndexer) extends Serializable
