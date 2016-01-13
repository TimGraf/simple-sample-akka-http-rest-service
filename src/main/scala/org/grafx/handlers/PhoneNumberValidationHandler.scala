package org.grafx.handlers

import java.net.URLEncoder
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.MediaType.NotCompressible
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.RawHeader
import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Source, Flow}
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.pattern.after
import akka.util.ByteString
import com.typesafe.scalalogging.StrictLogging
import org.grafx.protocols.ValidateResponseProtocol
import org.grafx.shapes.ValidateResponse
import org.grafx.utils.config.PhoneNumberServiceConfig
import scala.concurrent.{TimeoutException, ExecutionContext, Future}
import scala.concurrent.duration._

object PhoneNumberValidationHandler extends PhoneNumberServiceConfig with ValidateResponseProtocol with StrictLogging {
  def pnConnectionFlow()(implicit as: ActorSystem, mt: Materializer): Flow[HttpRequest, HttpResponse, Future[Http.OutgoingConnection]] = Http().outgoingConnectionTls(apiUrl.getHost)
  def pnFutureResponse(request: HttpRequest)(implicit as: ActorSystem, mt: Materializer): Future[HttpResponse]                         = Source.single(request).via(pnConnectionFlow).runWith(Sink.head)

  /*
     Sample curl command for the call

     curl -X POST --include 'https://neutrinoapi-phone-validate.p.mashape.com/phone-validate' \
     -H 'X-Mashape-Key: <api-key>' \
     -H 'Content-Type: application/x-www-form-urlencoded' \
     -H 'Accept: application/json' \
     -d 'country-code=us' \
     -d 'number=+18316561725'
   */
  def validate(numberString: String)(implicit as: ActorSystem, mt: Materializer, ec: ExecutionContext): Future[ValidateResponse] = {
    val data: ByteString          = ByteString(s"country-code=us&number=${URLEncoder.encode(numberString, "UTF-8")}")
    val headers: List[HttpHeader] = List(RawHeader("X-Mashape-Key", apiKey), RawHeader("Accept", "application/json"))
    val entity: RequestEntity     = HttpEntity(data).withContentType(MediaType.customBinary("application", "x-www-form-urlencoded", comp = NotCompressible))
    val pnRequest: HttpRequest    = HttpRequest(POST, apiUrl.getPath, headers, entity)

    val responseWithTimeout = Future.firstCompletedOf(
      pnFutureResponse(pnRequest) ::
      after(serviceTimeout.second, as.scheduler)(Future.failed(new TimeoutException)) ::
      Nil
    )

    responseWithTimeout.flatMap { response =>
      response.status match {
        case OK =>
          Unmarshal(response.entity).to[ValidateResponse].flatMap {
            case validateResponse: ValidateResponse => Future.successful(validateResponse)
          }

        case _ =>
          val message = s"Phone number validation failed: ${response.status}"

          logger.info(message)
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
