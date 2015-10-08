package org.grafx.simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import scala.language.postfixOps

class LoadSimulation extends Simulation {

  val parallelism = 30

  object Health {

    val httpConf = http
      .baseURL("http://localhost:9000")

    val health = exec(http("Health Check")
      .get("/health"))
      .pause(5)
  }

  val scn = scenario("Health").exec(Health.health)

  setUp(
    scn.inject(rampUsers(parallelism) over (10 seconds))
  ).protocols(Health.httpConf)
}