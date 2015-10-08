package org.grafx.shapes

import org.scalatest.{Matchers, FlatSpec}
import spray.json._

class HealthResponseSpec extends FlatSpec with Matchers with HealthResponseProtocol {
  def sampleJson: JsValue = s"""{ "status": "ok" }""".parseJson

  "HealthDetails" should "un-marshal from JSON" in {
    val healthResponse: HealthResponse = sampleJson.convertTo[HealthResponse]

    healthResponse.status should be ("ok")
  }

  "HealthResponse" should "marshal to JSON" in {
    val healthResponse: HealthResponse = new HealthResponse("ok")
    val json: JsValue                  = healthResponse.toJson

    json should be (sampleJson)
  }
}
