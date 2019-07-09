package io.prediction.opennlp.engine

import org.apache.predictionio.controller.P2LAlgorithm
import opennlp.maxent.GIS
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

class Algorithm(val ap: AlgorithmParams)
  extends P2LAlgorithm[PreparedData, Model, Query, PredictedResult] {

  def train(sc: SparkContext, data: PreparedData): Model = {
   Model(GIS.trainModel(ap.iteration, data.dataIndexer, ap.smoothing))

  }

  def predict(model: Model, query: Query): PredictedResult = {
    val interest = model.gis.getBestOutcome(model.gis.eval(query.text.split(" ")))
    PredictedResult(interest)
  }

  override def batchPredict(model: Model, qs: RDD[(Long, Query)]): RDD[(Long, PredictedResult)] = {
    qs.sparkContext.parallelize(
      qs.collect().map { case (index, query) =>
        (index, predict(model, query))
      })
  }

}
