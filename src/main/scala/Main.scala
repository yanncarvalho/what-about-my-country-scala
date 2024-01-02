import akka.actor.ActorSystem
import apis.ExternalApi
import models.{ApiResponse, CountryKeyAndNameResponse, CountryResponse}
import utils.JsonUnmarshal.given

import scala.concurrent.{Await, ExecutionContextExecutor}
import scala.concurrent.duration.Duration
@main def main(): Unit =
  given system: ActorSystem = ActorSystem()
  given executionContext: ExecutionContextExecutor = system.dispatcher
  val result = ExternalApi.getFromNetIndicator("BR")
  result.onComplete{
    println(_)
  }