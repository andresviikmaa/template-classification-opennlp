package io.prediction.opennlp.engine

import org.apache.predictionio.controller.{Engine, EngineFactory, IEngineFactory}

object EngineFactory extends EngineFactory {
  def apply() = {
    new Engine(
      classOf[DataSource],
      classOf[Preparator],
      Map("algo" -> classOf[Algorithm]),
      classOf[Serving])
  }
}
