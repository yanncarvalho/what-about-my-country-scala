package apis

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding.Get
import models.{
  Country,
  Indicator,
  CountryKeyAndName,
  CountryKeyAndNameResponse,
  CountryResponse,
  PaginationResponse
}
import spray.json.*
import utils.JsonUnmarshal.given

import scala.concurrent.{ExecutionContextExecutor, Future}

object ExternalApi:
  given system: ActorSystem                        = ActorSystem()
  given executionContext: ExecutionContextExecutor = system.dispatcher

  def getKeyAndNameFromNet: Future[Seq[CountryKeyAndName]] =
    val resp = getRequestWBankAPI[CountryKeyAndNameResponse](
      "api.worldbank.org/v2/",
      Seq("country")
    )
    resp.head.map(_.countries)

  private def getRequestWBankAPI[T](apiUrlBase: String, requests: Seq[String])(
      using reader: RootJsonReader[T]
  ): Seq[Future[T]] =
    requests.map { req =>
      requestWBankAPI[PaginationResponse](apiUrlBase, req, perPage = 1)
        .map[Int](_.pagination.total)
        .flatMap[T](requestWBankAPI[T](apiUrlBase, req, _))
    }

  private def requestWBankAPI[T](
      baseUrl: String,
      request: String,
      perPage: Int
  )(using reader: RootJsonReader[T]): Future[T] =

    val req =
      Get(uri = s"https://$baseUrl/$request?format=json&per_page=$perPage")
    Http()
      .singleRequest(req)
      .flatMap(res =>
        if res.status.isSuccess() then
          res.entity.dataBytes
            .runFold("")(_ + _.utf8String)
            .map(_.parseJson.convertTo[T])
        else
          Future.failed(
            RuntimeException(s"Request failed with status: ${res.status}")
          )
      )
  def getFromNet(key: String): Future[Country] =
    val resp = getRequestWBankAPI[CountryResponse](
      "api.worldbank.org/v2/",
      Seq(s"country/$key")
    )
    resp.head.map(_.countries).map(_.head)

  def getFromNetIndicator(key: String): Future[Indicator] =
    getRequestWBankAPI[Indicator](
      "api.worldbank.org/v2/",
      Seq(s"country/$key/indicator/SP.POP.TOTL")
    ).head
