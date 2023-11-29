package utils

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding.Get
import akka.serialization.jackson.JsonSerializable

import scala.concurrent.{ExecutionContextExecutor, Future}

case class Country(
    id: String,
    iso2Code: String,
    name: String,
    region: Region,
    adminregion: Region,
    incomeLevel: Region,
    lendingType: Region,
    capitalCity: String,
    longitude: String,
    latitude: String
)extends JsonSerializable

case class Region(
    id: String,
    iso2code: String,
    value: String
)extends JsonSerializable

case class RootInterface(
    Country: Country,
    page: Int,
    pages: Int,
    per_page: String,
    total: Int
)extends JsonSerializable


object JsonRequest:

  def getRequestWBankAPI(): Unit =
    requestWBankAPI("api.worldbank.org/v2/country", "BR", 1)

  private def requestWBankAPI(
      baseUrl: String,
      request: String,
      perPage: Integer
  ): Unit =
    given system: ActorSystem = ActorSystem()
    given executionContext: ExecutionContextExecutor = system.dispatcher
    val req = Get(uri = s"https://$baseUrl/$request?format=json&per_page=$perPage")
     Http().singleRequest(req)
           .flatMap(res =>
      if res.status.isSuccess() then
        res.entity.dataBytes.runFold("")(_+_.utf8String)
      else
        Future.failed(
          RuntimeException(s"Request failed with status: ${res.status}")
        )
    ).onComplete{
      body =>
        println(body)
    }
