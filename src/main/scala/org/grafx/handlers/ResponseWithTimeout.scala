package org.grafx.handlers

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpResponse
import akka.stream.Materializer
import akka.pattern.after
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future, TimeoutException}

trait ResponseWithTimeout {

  implicit class ResponseTimeout(fResponse: Future[HttpResponse])(implicit as: ActorSystem, mt: Materializer, ec: ExecutionContext) {

    def withTimeout(timeoutInSeconds: Int): Future[HttpResponse] = {
      val fTimeout = after(timeoutInSeconds.second, as.scheduler)(Future.failed(new TimeoutException))

      Future.firstCompletedOf(Seq(fResponse, fTimeout))
    }
  }
}
