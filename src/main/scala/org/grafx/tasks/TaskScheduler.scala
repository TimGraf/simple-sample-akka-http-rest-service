package org.grafx.tasks

import akka.actor.ActorSystem
import com.typesafe.scalalogging.StrictLogging
import org.grafx.utils.config.SchedulerConfig
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

trait TaskScheduler extends SchedulerConfig with StrictLogging {
  implicit val system: ActorSystem

  def startTaskScheduler() = {
    logger.info("Task scheduler started ...")

    implicit val schedulerExecutor: ExecutionContext = system.dispatchers.lookup("task-scheduler")

    system.scheduler.schedule(taskSchedulerInterval.minutes, taskSchedulerInterval.minutes) {
      logger.info("Performing scheduled tasks ...")

    }
  }
}
