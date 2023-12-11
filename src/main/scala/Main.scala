import apis.ExternalApi
import models.ApiResponse
import utils.JsonUnmarshal.given
@main def main(): Unit =
  val result = ExternalApi.getRequestWBankAPI[ApiResponse]
  println(result)
