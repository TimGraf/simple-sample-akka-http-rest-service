package org.grafx.shapes

import spray.json.DefaultJsonProtocol

case class HealthResponse(status: String)

trait HealthResponseProtocol extends DefaultJsonProtocol {
  implicit val healthResponseFormat = jsonFormat1(HealthResponse.apply)
}