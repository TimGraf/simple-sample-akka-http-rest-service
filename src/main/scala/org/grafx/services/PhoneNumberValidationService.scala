package org.grafx.services

import akka.event.Logging._
import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.stream.Materializer
import com.typesafe.scalalogging.StrictLogging
import io.swagger.annotations._
import javax.ws.rs.Path

import scala.concurrent.ExecutionContextExecutor
import ch.megard.akka.http.cors.CorsDirectives._
import ch.megard.akka.http.cors.CorsSettings


@Api(value = "/", produces = "application/json")
@Path("/")
trait PhoneNumberValidationService extends ResponseMarshaller with StrictLogging {
  implicit val actorSystem: ActorSystem
  implicit val executor: ExecutionContextExecutor
  implicit val materializer: Materializer

  val phoneNumberValidationRoutes = cors(CorsSettings.defaultSettings) {
    logRequestResult("phone-number-validation-service", InfoLevel) {
      getValidate ~ getHealth ~ getVersion
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

  @Path("version")
  @ApiOperation(httpMethod = "GET", value = "Returns the version for this service")
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "OK"),
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def getVersion = path("version") {
    get {
      complete(marshalVersionResponse)
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
