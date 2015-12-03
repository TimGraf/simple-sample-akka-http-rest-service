package org.grafx

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.http.scaladsl.Http
import com.typesafe.scalalogging.StrictLogging
import java.util.concurrent.Executors
import java.util.concurrent.ExecutorService
import org.grafx.services.PhoneNumberValidationService
import org.grafx.tasks.TaskScheduler
import org.grafx.utils.config.{ServiceConfig, MessagesConfig}
import scala.concurrent.{ExecutionContextExecutor, ExecutionContext}
import scala.language.postfixOps

object Boot extends App with PhoneNumberValidationService with TaskScheduler with ServiceConfig with MessagesConfig with StrictLogging {
  val processors: Int                  = Runtime.getRuntime.availableProcessors
  val executorService: ExecutorService = Executors.newFixedThreadPool(processors)

  logger.info(s"$messageApplicationName started ...")
  logger.info(s"ExecutorService using a pool size of: $processors")

  override implicit val system: ActorSystem                = ActorSystem("phone-number-validation", config)
  override implicit val materializer: ActorMaterializer    = ActorMaterializer()
  override implicit val executor: ExecutionContextExecutor = ExecutionContext.fromExecutorService(executorService)

  Http().bindAndHandle(phoneNumberValidationRoutes, serviceInterface, servicePort)

  startTaskScheduler()
}