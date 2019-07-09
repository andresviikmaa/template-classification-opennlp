package io.prediction.opennlp.engine

case class ItemScore(category: String, confidence: Double)

case class PredictedResult(itemScores: Array[ItemScore]) extends Serializable
