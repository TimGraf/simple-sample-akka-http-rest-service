package org.grafx.protocols

import com.typesafe.scalalogging.StrictLogging
import org.grafx.shapes.ValidateResponse
import spray.json._

trait ValidateResponseProtocol extends DefaultJsonProtocol with NullOptions with StrictLogging {

  implicit def validateResponseJsonFormat = new RootJsonFormat[ValidateResponse] {

    def write(validateResponse: ValidateResponse): JsObject = JsObject(
      "valid"                      -> JsBoolean(validateResponse.valid),
      "location"                   -> JsString(validateResponse.location.getOrElse("")),
      "international-number"       -> JsString(validateResponse.internationalNumber.getOrElse("")),
      "international-calling-code" -> JsString(validateResponse.internationalCallingCode.getOrElse("")),
      "type"                       -> JsString(validateResponse.numberType),
      "is-mobile"                  -> JsBoolean(validateResponse.isMobile.getOrElse(false)),
      "local-number"               -> JsString(validateResponse.localNumber.getOrElse("")),
      "country-code"               -> JsString(validateResponse.countryCode.getOrElse(""))
    )

    def read(value: JsValue): ValidateResponse = value.asJsObject.getFields(
      "valid",
      "location",
      "international-number",
      "international-calling-code",
      "type",
      "is-mobile",
      "local-number",
      "country-code") match {
        case Seq(
          JsBoolean(valid),
          JsString(location),
          JsString(internationalNumber),
          JsString(internationalCallingCode),
          JsString(numberType),
          JsBoolean(isMobile),
          JsString(localNumber),
          JsString(countryCode)) => ValidateResponse(valid, Some(location), Some(internationalNumber), Some(internationalCallingCode), numberType, Some(isMobile), Some(localNumber), Some(countryCode))

        case Seq(
          JsBoolean(valid),
          JsString(numberType)) => ValidateResponse(valid, None, None, None, numberType, None, None, None)

        case e => deserializationError(s"Expected ValidateResponse as JsObject, but got $e")
    }
  }
}
