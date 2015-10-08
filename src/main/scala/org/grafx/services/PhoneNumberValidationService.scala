package org.grafx.services

import akka.event.Logging._
import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.server.Directives._
import akka.stream.Materializer
import com.typesafe.scalalogging.StrictLogging
import org.grafx.handlers.PhoneNumberValidationHandler
import org.grafx.shapes.{ValidateResponseProtocol, HealthResponse, HealthResponseProtocol}
import scala.concurrent.ExecutionContextExecutor

trait PhoneNumberValidationService extends HealthResponseProtocol with ValidateResponseProtocol with StrictLogging {
  implicit val system: ActorSystem
  implicit val executor: ExecutionContextExecutor
  implicit val materializer: Materializer

  val libraryAssetsRoutes = {
    logRequestResult("phone-number-validation-service", InfoLevel) {
      path("health") {
        get {
          complete(ToResponseMarshallable(new HealthResponse("ok")))
        }
      } ~
      pathPrefix("validate") {
        (get & path(Segment)) { numberString =>
          complete {
            for {
              result <- PhoneNumberValidationHandler.validate(numberString)
            } yield ToResponseMarshallable(result)
          }
        }
      }
    }
  }
}
