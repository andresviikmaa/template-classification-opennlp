package io.prediction.opennlp.engine

import java.util

import grizzled.slf4j.Logger
import opennlp.tools.ml.model.{AbstractDataIndexer, OnePassDataIndexer}
import opennlp.tools.util.TrainingParameters
import org.apache.predictionio.controller.{EmptyEvaluationInfo, PDataSource}
import org.apache.spark.SparkContext
import org.apache.predictionio.data.store.PEventStore

import Array._
import scala.util.Random

class DataSource(val dsp: DataSourceParams) extends PDataSource[
  TrainingData,
  EmptyEvaluationInfo,
  Query,
  String] {

  val Separator = "¤"
  @transient lazy implicit val logger: Logger = Logger[this.type]

  override def readTraining(sc: SparkContext): TrainingData = {
    val trainingTreeStrings = allPhraseandInterests(sc)
    TrainingData(phraseAndInterestToTrainingData(trainingTreeStrings))
  }

  private def allPhraseandInterests(sc: SparkContext) : Seq[(String, String)] = {
    logger.info(s"Event names ${ dsp.eventNames}")

    //Get RDD of Events.
    PEventStore.find(
      appName = dsp.appName,
      entityType = Some("source"), // specify data entity type
      eventNames = Some(dsp.eventNames) // specify data event name

      // Convert collected RDD of events to and RDD of Observation
      // objects.
    )(sc).map(e => {
      val Interest = e.event
      val phrase = e.properties.get[String]("query")
      (Interest, phrase)

    }).collect().toSeq
  }

  private def phraseAndInterestToTrainingData(phraseAndInterests: Seq[(String, String)]) = {

    val eventStream = new SeqDataStream(phraseAndInterests)
    val dataIndexer = new OnePassDataIndexer()
    val params = new util.HashMap[String, String]();
    params.put(AbstractDataIndexer.CUTOFF_PARAM, dsp.cutoff.toString)

    dataIndexer.init(new TrainingParameters(params), null)
    dataIndexer.index(eventStream)

    dataIndexer
  }
}
