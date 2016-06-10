package org.grafx.handlers

import com.typesafe.scalalogging.StrictLogging
import org.grafx.shapes.VersionResponse
import scala.concurrent.{ExecutionContext, Future}

object VersionHandler extends StrictLogging {

  def getVersion()(implicit ec: ExecutionContext): Future[VersionResponse] = {
    Future {
      VersionResponse(Option(getClass.getPackage.getImplementationVersion).getOrElse("Not Available"))
    }
  }
}
