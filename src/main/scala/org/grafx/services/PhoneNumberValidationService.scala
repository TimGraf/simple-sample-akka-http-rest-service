package org.grafx.services

import akka.event.Logging._
import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.stream.Materializer

import com.typesafe.scalalogging.StrictLogging

import io.swagger.annotations._

import javax.ws.rs.Path

import scala.concurrent.ExecutionContextExecutor

@Api(value = "/", produces = "application/json")
@Path("/")
trait PhoneNumberValidationService extends ResponseMarshaller with StrictLogging {
  implicit val actorSystem: ActorSystem
  implicit val executor: ExecutionContextExecutor
  implicit val materializer: Materializer

  val phoneNumberValidationRoutes = {
    logRequestResult("phone-number-validation-service", InfoLevel) {
      getValidate ~ getHealth
    }
  }

  @Path("health")
  @ApiOperation(httpMethod = "GET", value = "Returns a health response for this service")
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "OK"),
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def getHealth = path("health") {
    get {
      complete(marshalHealthResponse)
    }
  }

  @Path("validate/{numberString}")
  @ApiOperation(httpMethod = "GET", value = "Returns a validation response based on phone number")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "numberString", required = true, dataType = "string", paramType = "path", value = "phone number to be validated")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "OK"),
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def getValidate = pathPrefix("validate") {
    (get & path(Segment)) { numberString =>
      complete(marshalValidateResponse(numberString))
    }
  }
}
