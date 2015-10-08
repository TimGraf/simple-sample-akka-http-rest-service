package org.grafx.utils.config

trait PhoneNumberServiceConfig extends BaseConfig {
  val mashApeKey: String = config.getString("mashape-key")
}