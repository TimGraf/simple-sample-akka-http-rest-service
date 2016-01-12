package org.grafx.services

import java.util.concurrent.TimeoutException
import akka.actor.ActorSystem
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model._
import akka.stream.Materializer
import org.grafx.handlers.{PhoneNumberValidationHandler, HealthHandler}
import org.grafx.protocols.{ValidateResponseProtocol, HealthResponseProtocol}
import com.typesafe.scalalogging.StrictLogging
import spray.json._
import scala.concurrent.ExecutionContextExecutor

trait Protocols extends HealthResponseProtocol with ValidateResponseProtocol

trait ResponseMarshaller extends Protocols with StrictLogging {
  implicit val system: ActorSystem
  implicit def executor: ExecutionContextExecutor
  implicit val materializer: Materializer

  def marshalHealthResponse: ToResponseMarshallable = {
    (
      for {
        health <- HealthHandler.getHealth
      } yield ToResponseMarshallable(health)
    ) recover {
      case _ => ToResponseMarshallable(HttpResponse(StatusCodes.InternalServerError))
    }
  }

  def marshalValidateResponse(numberString: String): ToResponseMarshallable = {
    (
      for {
        result <- PhoneNumberValidationHandler.validate(numberString)
      } yield ToResponseMarshallable(HttpResponse(status = StatusCodes.OK, entity = HttpEntity(MediaTypes.`application/json`, result.toJson.compactPrint)))
    ) recover {
      case tex: TimeoutException => ToResponseMarshallable(HttpResponse(StatusCodes.ServiceUnavailable))
      case _                     => ToResponseMarshallable(HttpResponse(StatusCodes.InternalServerError))
    }
  }
}
