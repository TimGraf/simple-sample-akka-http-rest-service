package org.grafx.tasks

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.typesafe.scalalogging.StrictLogging
import org.grafx.handlers.{ClientError, PhoneNumberValidationHandler}
import org.grafx.protocols.ValidateResponseProtocol
import org.grafx.shapes.ValidateResponse
import org.grafx.utils.cats.xor.{Bad, Good, Or}
import org.grafx.utils.config.SchedulerConfig
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}
import scala.concurrent.duration._
import spray.json._

/*
 * Ideally the task scheduler would probably be well abstracted from any business logic, domain objects, or serialization.
 * This was just meant to be an example of a scheduled task that would run periodically along with the service
 * as an example of performing some batch processing.
 */

trait TaskScheduler extends SchedulerConfig with StrictLogging with ValidateResponseProtocol {
  implicit val system: ActorSystem
  implicit def executor: ExecutionContextExecutor
  implicit val materializer: Materializer

  // List of phone numbers to validate would come from some persistence
  val batchPhoneNumbers = List("4155539755", "4155583200", "4153552600")

  def startTaskScheduler() = {
    logger.info("Task scheduler started ...")

    implicit val schedulerExecutor: ExecutionContext = system.dispatchers.lookup("task-scheduler")

    system.scheduler.schedule(taskSchedulerInterval.minutes, taskSchedulerInterval.minutes) {
      logger.info("Performing scheduled tasks ...")

      Future.traverse(batchPhoneNumbers)(number => PhoneNumberValidationHandler.validate(number)).map {
        case responses: List[ClientError Or ValidateResponse] => responses.foreach {
          case Good(validateResponse: ValidateResponse) => logger.info(validateResponse.toJson.compactPrint)
          case Bad(error: ClientError)                  => logger.error(error.toString)
        }
      } recover {
        case error => logger.error(error.getLocalizedMessage)
      }
    }
  }
}
