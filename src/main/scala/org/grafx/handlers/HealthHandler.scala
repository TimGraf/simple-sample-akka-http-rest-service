package org.grafx.handlers

import com.typesafe.scalalogging.StrictLogging
import org.grafx.shapes.HealthResponse
import scala.concurrent.{ExecutionContext, Future}

object HealthHandler extends StrictLogging {

  // TODO this should be expanded upon
  def getHealth()(implicit ec: ExecutionContext): Future[HealthResponse] = {
    Future {
      HealthResponse("ok")
    }
  }
}
