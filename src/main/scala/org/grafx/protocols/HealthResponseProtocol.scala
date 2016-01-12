package org.grafx.protocols

import org.grafx.shapes.HealthResponse
import spray.json.DefaultJsonProtocol

trait HealthResponseProtocol extends DefaultJsonProtocol {
  implicit val healthResponseFormat = jsonFormat1(HealthResponse.apply)
}
