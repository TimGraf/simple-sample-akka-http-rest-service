package org.grafx.protocols

import org.grafx.shapes.VersionResponse
import spray.json.DefaultJsonProtocol

trait VersionResponseProtocol extends DefaultJsonProtocol {
  implicit val versionResponseFormat = jsonFormat1(VersionResponse.apply)
}