package org.grafx.services

import akka.actor.ActorSystem
import akka.event.Logging._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.stream.Materializer

import scala.concurrent.ExecutionContextExecutor

trait SwaggerUIAssetService {
  implicit val system: ActorSystem
  implicit val executor: ExecutionContextExecutor
  implicit val materializer: Materializer

  val swaggerUIAssetRoutes = {
    logRequestResult("swagger-ui-asset-service", InfoLevel) {
      pathPrefix("swagger") {
        getFromResourceDirectory("swagger") ~ pathSingleSlash(get(redirect("index.html", StatusCodes.PermanentRedirect)))
      }
    }
  }
}
