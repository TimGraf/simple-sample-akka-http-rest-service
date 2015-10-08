package org.grafx.utils.config

trait ServiceConfig extends BaseConfig {
  val serviceInterface: String = config.getString("http.interface")
  val servicePort: Int         = config.getInt("http.port")
}
