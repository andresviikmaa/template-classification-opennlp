package io.prediction.opennlp.engine

import org.apache.predictionio.controller.P2LAlgorithm
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import opennlp.tools.ml.maxent.GISTrainer

class Algorithm(val ap: AlgorithmParams)
  extends P2LAlgorithm[PreparedData, Model, Query, PredictedResult] {

  def train(sc: SparkContext, data: PreparedData): Model = {
    val trainer = new GISTrainer()
    trainer.setSmoothing(ap.smoothing)
   Model(trainer.trainModel(ap.iteration, data.dataIndexer))

  }

  def predict(model: Model, query: Query): PredictedResult = {
    val outcomes = model.gis.getAllOutcomes(model.gis.eval(query.text.split(" ")))

    val re = """(\w+)\[([\d.]+)\]""".r
    val itemScores = outcomes.split("  ").map(_ match {
      case re (label, prob) => ItemScore(label, prob.toDouble)
    })
    PredictedResult(itemScores)
  }

  override def batchPredict(model: Model, qs: RDD[(Long, Query)]): RDD[(Long, PredictedResult)] = {
    qs.sparkContext.parallelize(
      qs.collect().map { case (index, query) =>
        (index, predict(model, query))
      })
  }

}
