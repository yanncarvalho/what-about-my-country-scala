import apis.ExternalApi
import models.{ApiResponse, CountryKeyAndNameResponse, CountryResponse}
import utils.JsonUnmarshal.given

import scala.concurrent.Await
import scala.concurrent.duration.Duration
@main def main(): Unit =
  val result = ExternalApi.getKeyAndNameFromNet
  result.foreach(println)