package org.grafx.services

import akka.event.Logging._
import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.stream.Materializer
import com.typesafe.scalalogging.StrictLogging
import scala.concurrent.ExecutionContextExecutor

trait PhoneNumberValidationService extends ResponseMarshaller with StrictLogging {
  implicit val system: ActorSystem
  implicit val executor: ExecutionContextExecutor
  implicit val materializer: Materializer

  val phoneNumberValidationRoutes = {
    logRequestResult("phone-number-validation-service", InfoLevel) {
      path("health") {
        get {
          complete(marshalHealthResponse)
        }
      } ~
      pathPrefix("validate") {
        (get & path(Segment)) { numberString =>
          complete(marshalValidateResponse(numberString))
        }
      }
    }
  }
}
