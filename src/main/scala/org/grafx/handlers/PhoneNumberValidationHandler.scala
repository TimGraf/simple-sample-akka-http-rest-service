package org.grafx.handlers

import java.net.URLEncoder
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.RawHeader
import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Source, Flow}
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import com.typesafe.scalalogging.StrictLogging
import org.grafx.shapes.{ValidateResponseProtocol, ValidateResponse}
import org.grafx.utils.config.PhoneNumberServiceConfig
import scala.concurrent.{ExecutionContext, Future}

object PhoneNumberValidationHandler extends PhoneNumberServiceConfig with ValidateResponseProtocol with StrictLogging {
  /*
     Sample curl command for the call

     curl -X POST --include 'https://neutrinoapi-phone-validate.p.mashape.com/phone-validate' \
     -H 'X-Mashape-Key: X5K8pUwWNymshdZKvmqAGmP0BkNPp1PkPOrjsnmddtF9CGGwnN' \
     -H 'Content-Type: application/x-www-form-urlencoded' \
     -H 'Accept: application/json' \
     -d 'country-code=us' \
     -d 'number=+18316561725'
   */
  def validate(numberString: String)(implicit as: ActorSystem, mt: Materializer, ec: ExecutionContext): Future[ValidateResponse] = {
    val pnConnectionFlow: Flow[HttpRequest, HttpResponse, Future[Http.OutgoingConnection]] = Http().outgoingConnectionTls(apiServiceHost, apiServicePort)
    def pnFutureResponse(request: HttpRequest): Future[HttpResponse]                       = Source.single(request).via(pnConnectionFlow).runWith(Sink.head)
    val pnRequest: HttpRequest                                                             = HttpRequest(POST, validateEndpoint)
      .withHeaders(
        RawHeader("X-Mashape-Key", apiKey),
        RawHeader("Accept", "application/json")
      )
      .withEntity(
        HttpEntity(ContentType(MediaTypes.`application/x-www-form-urlencoded`), s"country-code=us&number=${URLEncoder.encode(numberString, "UTF-8")}")
      )

    pnFutureResponse(pnRequest).flatMap { response =>
      response.status match {
        case OK =>
          Unmarshal(response.entity).to[ValidateResponse].flatMap {
            case validateResponse: ValidateResponse => Future.successful(validateResponse)
          }

        case _ =>
          val message = s"Phone number validation failed: ${response.status}"

          logger.debug(message)
          Future.failed(new Exception(message))
      }
    } recoverWith  {
      case error => Future.successful(createInvalidResponse())
    }
  }

  private def createInvalidResponse(): ValidateResponse = {
    new ValidateResponse(
      valid = false,
      countryCode = "",
      internationalNumber = "",
      location = "",
      localNumber = "",
      numberType = "invalid",
      internationalCallingCode = "",
      isMobile = false
    )
  }
}
