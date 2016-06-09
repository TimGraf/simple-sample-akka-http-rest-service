package org.grafx

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.http.scaladsl.Http
import com.typesafe.scalalogging.StrictLogging
import java.util.concurrent.Executors
import java.util.concurrent.ExecutorService

import akka.http.scaladsl.server.Directives._
import org.grafx.services.{PhoneNumberValidationService, SwaggerUIAssetService, SwaggerDocService}
import org.grafx.tasks.TaskScheduler
import org.grafx.utils.config.{MessagesConfig, ServiceConfig}

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}
import scala.language.postfixOps

object Boot extends App with PhoneNumberValidationService with SwaggerUIAssetService with ServiceConfig with TaskScheduler with MessagesConfig with StrictLogging {
  val processors: Int                  = Runtime.getRuntime.availableProcessors
  val executorService: ExecutorService = Executors.newFixedThreadPool(processors)

  logger.info(s"$messageApplicationName started ...")
  logger.info(s"ExecutorService using a pool size of: $processors")

  override implicit val system: ActorSystem                = ActorSystem("phone-number-validation", config)
  override implicit val materializer: ActorMaterializer    = ActorMaterializer()
  override implicit val executor: ExecutionContextExecutor = ExecutionContext.fromExecutorService(executorService)

  val routesWithSwagger = swaggerUIAssetRoutes ~ phoneNumberValidationRoutes ~ new SwaggerDocService[PhoneNumberValidationService](system, serviceInterface, servicePort).routes

  Http().bindAndHandle(routesWithSwagger, serviceInterface, servicePort)

  startTaskScheduler()
}