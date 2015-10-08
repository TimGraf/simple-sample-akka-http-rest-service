package org.grafx.shapes

import org.scalatest.{FlatSpec, Matchers}
import spray.json._

class ValidateResponseSpec extends FlatSpec with Matchers with ValidateResponseProtocol {
  def sampleJson: JsValue =
    s"""
       |{
       |    "valid": true,
       |    "country-code": "US",
       |    "international-number": "+19256994897",
       |    "location": "California",
       |    "local-number": "(925) 699-4897",
       |    "type": "unknown",
       |    "international-calling-code": "1",
       |    "is-mobile": false
       |}
     """.stripMargin.parseJson

  "ValidateResponse" should "un-marshal from JSON" in {
    val validateResponse: ValidateResponse = sampleJson.convertTo[ValidateResponse]

    validateResponse.valid should be (true)
    validateResponse.localNumber should be ("(925) 699-4897")
  }

  "ValidateResponse" should "marshal to JSON" in {
    val validateResponse: ValidateResponse = new ValidateResponse(
      valid = true,
      countryCode = "US",
      internationalNumber = "+19256994897",
      location = "California",
      localNumber = "(925) 699-4897",
      numberType = "unknown",
      internationalCallingCode = "1",
      isMobile = false
    )
    val json: JsValue = validateResponse.toJson

    json should be (sampleJson)
  }
}
