package org.grafx.protocols

import org.grafx.shapes.ValidateResponse
import spray.json._

trait ValidateResponseProtocol extends DefaultJsonProtocol {

  implicit def validateResponseJsonFormat = new RootJsonFormat[ValidateResponse] {

    def write(validateResponse: ValidateResponse): JsObject = JsObject(
      "valid"                      -> JsBoolean(validateResponse.valid),
      "location"                   -> JsString(validateResponse.location),
      "international-number"       -> JsString(validateResponse.internationalNumber),
      "international-calling-code" -> JsString(validateResponse.internationalCallingCode),
      "type"                       -> JsString(validateResponse.numberType),
      "is-mobile"                  -> JsBoolean(validateResponse.isMobile),
      "local-number"               -> JsString(validateResponse.localNumber),
      "country-code"               -> JsString(validateResponse.countryCode)
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
          JsString(countryCode)) => ValidateResponse(valid, location, internationalNumber, internationalCallingCode, numberType, isMobile, localNumber, countryCode)

        case e => deserializationError(s"Expected ValidateResponse as JsObject, but got $e")
    }
  }
}
