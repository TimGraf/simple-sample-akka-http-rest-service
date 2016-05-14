package org.grafx.handlers

sealed trait ClientError

object ClientError {
  final case class ServiceError(message: String) extends ClientError
}
