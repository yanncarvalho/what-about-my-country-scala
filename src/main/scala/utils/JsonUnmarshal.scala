package utils

import models.{Country, CountrySubInfo, Pagination, ApiResponse}
import spray.json.{
  DefaultJsonProtocol,
  JsArray,
  JsString,
  JsNumber,
  JsValue,
  JsonFormat,
  RootJsonFormat,
  deserializationError
}

object JsonUnmarshal extends DefaultJsonProtocol:
  given personFormat: JsonFormat[Pagination] = jsonFormat4(
    Pagination.apply
  )
  given countryFormat: JsonFormat[Country] = jsonFormat10(Country.apply)
  given CountrySubInfoFormat: JsonFormat[CountrySubInfo] = jsonFormat3(
    CountrySubInfo.apply
  )
  given CountryResponseFormat: JsonFormat[ApiResponse] = jsonFormat2(
    ApiResponse.apply
  )

  given RootJsonFormat[ApiResponse] with
    def read(value: JsValue): ApiResponse =
      value match
        case JsArray(Vector(pagination, countries)) =>
          ApiResponse(
            pagination.convertTo[Pagination],
            countries.convertTo[List[Country]]
          )
        case err => deserializationError(s"Api Response expected $err")

    def write(c: ApiResponse): JsValue = ???

  given RootJsonFormat[Pagination] with
    def read(json: JsValue): Pagination = json.asJsObject
      .getFields("page", "pages", "per_page", "total") match
      case Seq(
            JsString(page),
            JsString(pages),
            JsString(perPage),
            JsString(total)
          ) =>
        Pagination(page.toInt, pages.toInt, perPage.toInt, total.toInt)
    def write(p: Pagination): JsValue = ???
  given RootJsonFormat[Country] with

    def read(value: JsValue): Country =
      value.asJsObject
        .getFields(
          "id",
          "iso2Code",
          "name",
          "capitalCity",
          "longitude",
          "latitude",
          "adminregion",
          "lendingType",
          "region",
          "incomeLevel"
        ) match

        case Seq(
              JsString(id),
              JsString(iso2Code),
              JsString(name),
              JsString(capitalCity),
              JsString(longitude),
              JsString(latitude),
              adminRegion,
              lendingType,
              region,
              incomeLevel
            ) =>
          val infoRegion      = region.convertTo[CountrySubInfo];
          val infoLendingType = lendingType.convertTo[CountrySubInfo];
          val infoIncomeLevel = incomeLevel.convertTo[CountrySubInfo];
          val infoAdminRegion = adminRegion.convertTo[CountrySubInfo];
          Country(
            id = id,
            iso2Code = iso2Code,
            name = name,
            capitalCity = capitalCity,
            longitude = longitude,
            latitude = latitude,
            region = infoRegion,
            adminRegion = infoAdminRegion,
            incomeLevel = infoIncomeLevel,
            lendingType = infoLendingType
          )
        case err => deserializationError(s"Country expected $err")

    def write(c: Country): JsValue = ???
