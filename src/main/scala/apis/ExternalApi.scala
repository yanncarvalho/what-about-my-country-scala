package apis

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding.Get
import spray.json.*
import scala.concurrent.duration.*
import scala.concurrent.{Await, ExecutionContextExecutor, Future}

object ExternalApi:

  def getRequestWBankAPI[T](using reader: RootJsonReader[T]): T =
    requestWBankAPI[T]("api.worldbank.org/v2/country", "BR", 1)

  private def requestWBankAPI[T](
      baseUrl: String,
      request: String,
      perPage: Integer
  )(using reader: RootJsonReader[T]): T =
    given system: ActorSystem = ActorSystem()

    given executionContext: ExecutionContextExecutor = system.dispatcher

    val req =
      Get(uri = s"https://$baseUrl/$request?format=json&per_page=$perPage")
    val result = Http()
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

    Await.result(result, 10.minutes)
