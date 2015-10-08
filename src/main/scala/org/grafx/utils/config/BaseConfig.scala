package org.grafx.utils.config

import java.io.File
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.StrictLogging

trait BaseConfig extends StrictLogging {

  def config = BaseConfig.config

  object BaseConfig {
    logger.info("Loading config file ...")

    val prop: String       = System.getProperty("config.file")
    val configFile: String = if (prop == null) { "src/main/resources/application.conf" } else { prop }
    val config: Config     = ConfigFactory.load(ConfigFactory.parseFileAnySyntax(new File(configFile)))
  }
}
