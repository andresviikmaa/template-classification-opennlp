package io.prediction.opennlp.engine

import opennlp.tools.ml.model.Event
import opennlp.tools.util.ObjectStream

class SeqDataStream(val data: Seq[(String, String)]) extends ObjectStream[Event]
{
  private var next = 0

  def hasNext: Boolean = {
    data.size > next
  }

  override def read(): Event = {
    if (!hasNext) {
      null
    } else {
      val nextToken = data(next)
      next += 1
      new Event(nextToken._1, nextToken._2.split("\\s+"))
    }
  }
}
