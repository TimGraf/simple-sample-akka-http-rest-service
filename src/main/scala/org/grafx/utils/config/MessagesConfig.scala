package org.grafx.utils.config

trait MessagesConfig extends BaseConfig {
  val messageApplicationName: String = config.getString("messages.application.name")
}
