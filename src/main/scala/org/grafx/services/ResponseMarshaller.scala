package org.grafx.services

import java.util.concurrent.TimeoutException

import akka.actor.ActorSystem
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.{HttpEntity, _}
import akka.stream.Materializer
import org.grafx.handlers.{HealthHandler, PhoneNumberValidationHandler, VersionHandler}
import org.grafx.protocols.{HealthResponseProtocol, ValidateResponseProtocol, VersionResponseProtocol}
import com.typesafe.scalalogging.StrictLogging
import org.grafx.shapes.{HealthResponse, VersionResponse}
import org.grafx.utils.cats.xor.{Bad, Good}
import spray.json._

import scala.concurrent.ExecutionContextExecutor

trait Protocols extends HealthResponseProtocol with VersionResponseProtocol with ValidateResponseProtocol

trait ResponseMarshaller extends Protocols with StrictLogging {
  implicit val actorSystem: ActorSystem
  implicit def executor: ExecutionContextExecutor
  implicit val materializer: Materializer

  def marshalHealthResponse: ToResponseMarshallable = {
    HealthHandler.getHealth.map {
      case health: HealthResponse => ToResponseMarshallable(health)
      case _                      => ToResponseMarshallable(HttpResponse(StatusCodes.InternalServerError))
    } recover {
      case _ => ToResponseMarshallable(HttpResponse(StatusCodes.InternalServerError))
    }
  }

  def marshalVersionResponse: ToResponseMarshallable = {
    VersionHandler.getVersion.map {
      case version: VersionResponse => ToResponseMarshallable(version)
      case _                        => ToResponseMarshallable(HttpResponse(StatusCodes.InternalServerError))
    } recover {
      case _ => ToResponseMarshallable(HttpResponse(StatusCodes.InternalServerError))
    }
  }

  def marshalValidateResponse(numberString: String): ToResponseMarshallable = {
    PhoneNumberValidationHandler.validate(numberString).map {
      case Good(result) => ToResponseMarshallable(HttpResponse(status = StatusCodes.OK, entity = HttpEntity(MediaTypes.`application/json`, result.toJson.compactPrint)))
      case Bad(error)   => ToResponseMarshallable(HttpResponse(status = StatusCodes.ServiceUnavailable, entity = HttpEntity(error.toString)))
    } recover {
      case tex: TimeoutException => ToResponseMarshallable(HttpResponse(StatusCodes.ServiceUnavailable))
      case _                     => ToResponseMarshallable(HttpResponse(StatusCodes.InternalServerError))
    }
  }
}
