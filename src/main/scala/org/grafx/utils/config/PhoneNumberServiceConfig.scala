package org.grafx.utils.config

trait PhoneNumberServiceConfig extends BaseConfig {
  val apiKey: String           = config.getString("service.pnv.api-key")
  val validateEndpoint: String = config.getString("service.pnv.validate")
  val apiServicePort: Int      = config.getInt("service.pnv.port")
  val apiServiceHost: String   = config.getString("service.pnv.host")
}