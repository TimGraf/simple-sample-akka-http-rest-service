package org.grafx.utils.config

trait SchedulerConfig extends BaseConfig {
  val taskSchedulerInterval: Long = config.getLong("task-scheduler.interval-in-minutes")
}
