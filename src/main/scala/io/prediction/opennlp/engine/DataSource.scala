package io.prediction.opennlp.engine

import grizzled.slf4j.Logger
import org.apache.predictionio.controller.{EmptyEvaluationInfo, EmptyParams, PDataSource}
import org.apache.predictionio.data.storage.Storage
import opennlp.maxent.BasicEventStream
import opennlp.model.OnePassDataIndexer
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.predictionio.data.storage.DataMap
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
  /*
  private def allPhraseandInterests(sc: SparkContext): Seq[String] = {
    val events = Storage.getPEvents().find(appId = dsp.appId, entityType = Some("phrase"))(sc)
    events.map { event =>
      val phrase = event.properties.get[String]("phrase")
      val Interest = event.properties.get[String]("Interest").replace(" ","_")
      s"$phrase $Interest"
    }.collect().toSeq

  }
*/
  private def allPhraseandInterests(sc: SparkContext) : Seq[String] = {
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
      phrase + Separator + Interest

    }).collect().toSeq
  }

  private def phraseAndInterestToTrainingData(phraseAndInterests: Seq[String]) = {

    val eventStream = new BasicEventStream(new SeqDataStream(phraseAndInterests), Separator)
    val dataIndexer = new OnePassDataIndexer(eventStream, dsp.cutoff)

    dataIndexer
  }
}
