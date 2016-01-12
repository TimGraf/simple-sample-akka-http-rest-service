package org.grafx.utils.config

import java.net.URL

trait PhoneNumberServiceConfig extends BaseConfig {
  val apiKey: String      = config.getString("service.pnv.api-key")
  val apiUrl: URL         = new URL(config.getString("service.pnv.url"))
  val serviceTimeout: Int = config.getInt("service.pnv.timeout-in-seconds")
}