package org.grafx.utils.config

trait SwaggerConfig extends BaseConfig {
  val swaggerHost: String        = config.getString("swagger.host")
  val swaggerBasePath: String    = config.getString("swagger.basePath")
  val swaggerApiDocsPath: String = config.getString("swagger.apiDocsPath")
}
