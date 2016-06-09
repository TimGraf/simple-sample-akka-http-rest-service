package org.grafx.scheduler

import akka.actor.ActorSystem
import akka.stream.Materializer

import com.typesafe.scalalogging.StrictLogging

import org.grafx.handlers.BatchPhoneNumberValidationHandler
import org.grafx.utils.config.SchedulerConfig

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}
import scala.concurrent.duration._

trait TaskScheduler extends SchedulerConfig with StrictLogging {
  implicit val actorSystem: ActorSystem
  implicit def executor: ExecutionContextExecutor
  implicit val materializer: Materializer

  def startTaskScheduler() = {
    logger.info("Task scheduler started ...")

    implicit val schedulerExecutor: ExecutionContext = actorSystem.dispatchers.lookup("task-scheduler")

    actorSystem.scheduler.schedule(taskSchedulerInterval.minutes, taskSchedulerInterval.minutes) {
      logger.info("Performing scheduled tasks ...")

      BatchPhoneNumberValidationHandler.batchProcessPhoneNumbers()
    }
  }
}
