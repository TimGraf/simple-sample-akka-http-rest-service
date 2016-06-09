package org.grafx.handlers

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.typesafe.scalalogging.StrictLogging
import org.grafx.protocols.ValidateResponseProtocol
import org.grafx.shapes.ValidateResponse
import org.grafx.utils.cats.xor.{Bad, Good, _}
import spray.json._

import scala.concurrent.{ExecutionContext, Future}

object BatchPhoneNumberValidationHandler extends StrictLogging with ValidateResponseProtocol {

  def batchProcessPhoneNumbers()(implicit as: ActorSystem, mt: Materializer, ec: ExecutionContext) = {
    val batchPhoneNumbers = getPhoneNumbers()

    Future.traverse(batchPhoneNumbers)(number => PhoneNumberValidationHandler.validate(number)).map {
      case responses: List[ClientError Or ValidateResponse] => responses.foreach {
        case Good(validateResponse: ValidateResponse) => logger.info(validateResponse.toJson.compactPrint)
        case Bad(error: ClientError)                  => logger.error(error.toString)
      }
    } recover {
      case error => logger.error(error.getLocalizedMessage)
    }
  }

  private def getPhoneNumbers() = {
    // List of phone numbers to validate would come from some persistence
    List("4155539755", "4155583200", "4153552600")
  }
}
