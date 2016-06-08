package org.grafx.services

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.github.swagger.akka.model.Info
import com.github.swagger.akka.{HasActorSystem, SwaggerHttpService}

import scala.reflect.runtime.{universe => ru}

class SwaggerDocService(system: ActorSystem, serviceHost: String, servicePort: Int) extends SwaggerHttpService with HasActorSystem {
  override implicit val actorSystem: ActorSystem = system
  override implicit val materializer: ActorMaterializer = ActorMaterializer()

  override val apiTypes = Seq(ru.typeOf[PhoneNumberValidationService])    //voodoo
  override val host = s"localhost:$servicePort"                           //the url of your api, not swagger's json endpoint
  override val basePath = "/"                                             //the basePath for the API you are exposing
  override val apiDocsPath = "api-docs"                                   //where you want the swagger-json endpoint exposed
  override val info = Info(version = "1.0")
}
