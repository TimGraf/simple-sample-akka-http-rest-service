package org.grafx.services

import akka.actor.ActorSystem
import akka.event.Logging._
import akka.http.scaladsl.model.StatusCodes
import akka.stream.ActorMaterializer

import com.github.swagger.akka.model.Info
import com.github.swagger.akka.{HasActorSystem, SwaggerHttpService}

import scala.concurrent.ExecutionContextExecutor
import scala.reflect.runtime.universe._

import org.grafx.utils.config.SwaggerConfig

trait SwaggerService extends SwaggerConfig with SwaggerHttpService with HasActorSystem {
  implicit val actorSystem: ActorSystem
  implicit val executor: ExecutionContextExecutor
  implicit val materializer: ActorMaterializer
  implicit val apiTypes: Seq[Type]

  override val host = swaggerHost
  override val basePath = swaggerBasePath
  override val apiDocsPath = swaggerApiDocsPath
  override val info = Info(version = "1.0") //api info

  val swaggerUIAssetRoutes = {
    logRequestResult("swagger-service", InfoLevel) {
      pathPrefix("swagger") {
        getFromResourceDirectory("swagger") ~ pathSingleSlash(get(redirect("index.html", StatusCodes.PermanentRedirect)))
      }
    }
  }

  val swaggerRoutes = swaggerUIAssetRoutes ~ routes
}
