package org.grafx.shapes

import org.grafx.protocols.ValidateResponseProtocol
import org.scalatest.{FlatSpec, Matchers}
import spray.json._

class ValidateResponseSpec extends FlatSpec with Matchers with ValidateResponseProtocol {
  def sampleJson: JsValue =
    s"""
       |{
       |    "location": "Monterey, CA",
       |    "international-number": "+18316561725",
       |    "country-code": "US",
       |    "valid": true,
       |    "type": "unknown",
       |    "international-calling-code": "1",
       |    "is-mobile": false,
       |    "local-number": "(831) 656-1725"
       |}
     """.stripMargin.parseJson

  "ValidateResponse" should "un-marshal from JSON" in {
    val validateResponse: ValidateResponse = sampleJson.convertTo[ValidateResponse]

    validateResponse.valid should be (true)
    validateResponse.localNumber should be (Some("(831) 656-1725"))
  }

  "ValidateResponse" should "marshal to JSON" in {
    val validateResponse: ValidateResponse = new ValidateResponse(
      valid = true,
      countryCode = Some("US"),
      internationalNumber = Some("+18316561725"),
      location = Some("Monterey, CA"),
      localNumber = Some("(831) 656-1725"),
      numberType = "unknown",
      internationalCallingCode = Some("1"),
      isMobile = Some(false)
    )
    val json: JsValue = validateResponse.toJson

    json should be (sampleJson)
  }
}
