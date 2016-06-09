package org.grafx.handlers

import java.net.URLEncoder
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.MediaType.NotCompressible
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.RawHeader
import akka.stream.Materializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.util.ByteString
import com.typesafe.scalalogging.StrictLogging
import org.grafx.protocols.ValidateResponseProtocol
import org.grafx.shapes.ValidateResponse
import org.grafx.utils.config.PhoneNumberServiceConfig
import org.grafx.utils.cats.xor.{Bad, Good, Or}
import scala.concurrent.{ExecutionContext, Future}

object PhoneNumberValidationHandler extends PhoneNumberServiceConfig with ValidateResponseProtocol with ResponseWithTimeout with StrictLogging {

  def pnConnectionFlow()(implicit as: ActorSystem, mt: Materializer): Flow[HttpRequest, HttpResponse, Future[Http.OutgoingConnection]] = Http().outgoingConnectionHttps(apiUrl.getHost)
  def pnFutureResponse(request: HttpRequest)(implicit as: ActorSystem, mt: Materializer): Future[HttpResponse] = Source.single(request).via(pnConnectionFlow).runWith(Sink.head)

  def validate(numberString: String)(implicit as: ActorSystem, mt: Materializer, ec: ExecutionContext): Future[ClientError Or ValidateResponse] = {
    callValidationService(numberString).flatMap {
      case Good(responseEntity: ResponseEntity) => Unmarshal(responseEntity).to[ValidateResponse].flatMap {
        case validateResponse: ValidateResponse => Future.successful(Good(validateResponse))
      } recover {
        case _ => Bad(ClientError.ServiceError("Bad response from the validation service."))
      }
      case Bad(error: ClientError) => Future.successful(Bad(error))
    }
  }

  /*
     Sample curl command for the call

     curl -X POST --include 'https://neutrinoapi-phone-validate.p.mashape.com/phone-validate' \
     -H 'X-Mashape-Key: <api-key>' \
     -H 'Content-Type: application/x-www-form-urlencoded' \
     -H 'Accept: application/json' \
     -d 'country-code=us' \
     -d 'number=+18316561725'
   */
  private def callValidationService(numberString: String)(implicit as: ActorSystem, mt: Materializer, ec: ExecutionContext): Future[ClientError Or ResponseEntity] = {
    val data: ByteString          = ByteString(s"country-code=us&number=${URLEncoder.encode(numberString, "UTF-8")}")
    val headers: List[HttpHeader] = List(RawHeader("X-Mashape-Key", apiKey), RawHeader("Accept", "application/json"))
    val entity: RequestEntity     = HttpEntity(data).withContentType(MediaType.customBinary("application", "x-www-form-urlencoded", comp = NotCompressible))
    val pnRequest: HttpRequest    = HttpRequest(POST, apiUrl.getPath, headers, entity)

    pnFutureResponse(pnRequest).withTimeout(serviceTimeout).map {
        case HttpResponse(StatusCodes.OK, resHeaders, resEntity, _) => Good(resEntity)
        case HttpResponse(resCode, _, resEntity, _)                 => Bad(ClientError.ServiceError(s"Non OK response from the validation service. $resCode - $resEntity"))
    } recover {
      case error => Bad(ClientError.ServiceError(s"Error communicating with the validation service. ${error.getLocalizedMessage}"))
    }
  }
}
