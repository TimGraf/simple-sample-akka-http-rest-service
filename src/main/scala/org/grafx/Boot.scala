package org.grafx

import scala.language.postfixOps
import scala.reflect.runtime.{universe => ru}
import scala.reflect.runtime.universe._
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.http.scaladsl.Http

import com.typesafe.scalalogging.StrictLogging

import java.util.concurrent.Executors
import java.util.concurrent.ExecutorService

import org.grafx.services.{PhoneNumberValidationService, SwaggerService}
import org.grafx.scheduler.TaskScheduler
import org.grafx.utils.config.{MessagesConfig, ServiceConfig}


object Boot extends App with ServiceConfig with MessagesConfig with PhoneNumberValidationService with SwaggerService with TaskScheduler with StrictLogging {
  val processors: Int                  = Runtime.getRuntime.availableProcessors
  val executorService: ExecutorService = Executors.newFixedThreadPool(processors)

  logger.info(s"$messageApplicationName started ...")
  logger.info(s"ExecutorService using a pool size of: $processors")

  override implicit val actorSystem: ActorSystem           = ActorSystem("phone-number-validation", config)
  override implicit val materializer: ActorMaterializer    = ActorMaterializer()
  override implicit val executor: ExecutionContextExecutor = ExecutionContext.fromExecutorService(executorService)
  override implicit val apiTypes: Seq[Type]                = Seq(ru.typeOf[PhoneNumberValidationService]) // the service api used by swagger

  val routesWithSwagger = swaggerRoutes ~ phoneNumberValidationRoutes

  Http().bindAndHandle(routesWithSwagger, serviceInterface, servicePort)

  startTaskScheduler()
}