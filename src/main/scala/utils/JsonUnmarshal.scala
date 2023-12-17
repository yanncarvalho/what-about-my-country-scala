package utils

import models.{
  ApiResponse,
  Country,
  CountryKeyAndName,
  CountryKeyAndNameResponse,
  CountryResponse,
  CountrySubInfo,
  Pagination,
  PaginationResponse
}
import spray.json.{
  DefaultJsonProtocol,
  JsArray,
  JsNumber,
  JsString,
  JsValue,
  JsonFormat,
  RootJsonFormat,
  deserializationError
}

object JsonUnmarshal extends DefaultJsonProtocol:

  given CountrySubInfoFormat: JsonFormat[CountrySubInfo] = jsonFormat3(
    CountrySubInfo.apply
  )

  given RootJsonFormat[CountryResponse] with
    def read(value: JsValue): CountryResponse =
      value match
        case JsArray(Vector(_, countries)) =>
          CountryResponse(countries.convertTo[Seq[Country]])

        case err => deserializationError(s"Api Response expected $err")

    def write(c: CountryResponse): JsValue = ???

  given RootJsonFormat[PaginationResponse] with
    def read(value: JsValue): PaginationResponse =
      value match
        case JsArray(Vector(pagination, _)) =>
          PaginationResponse(pagination.convertTo[Pagination])

        case err => deserializationError(s"Api Response expected $err")

    def write(c: PaginationResponse): JsValue = ???

  given RootJsonFormat[ApiResponse] with
    def read(value: JsValue): ApiResponse =
      value match
        case JsArray(Vector(pagination, countries)) =>
          ApiResponse(
            pagination.convertTo[Pagination],
            countries.convertTo[Seq[Country]]
          )
        case err => deserializationError(s"Api Response expected $err")

    def write(c: ApiResponse): JsValue = ???

  given RootJsonFormat[Pagination] with
    def read(json: JsValue): Pagination =
      json.asJsObject
        .getFields("page", "pages", "per_page", "total") match
        case Seq(
              JsNumber(page),
              JsNumber(pages),
              JsString(perPage),
              JsNumber(total)
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

  given RootJsonFormat[CountryKeyAndNameResponse] with

    def read(value: JsValue): CountryKeyAndNameResponse =
      value match
        case JsArray(Vector(_, countries)) =>
          CountryKeyAndNameResponse(countries.convertTo[Seq[CountryKeyAndName]])

        case err => deserializationError(s"Api Response expected $err")
    def write(c: CountryKeyAndNameResponse): JsValue = ???

  given RootJsonFormat[CountryKeyAndName] with

    def read(value: JsValue): CountryKeyAndName =
      value.asJsObject
        .getFields(
          "iso2Code",
          "name"
        ) match

        case Seq(
              JsString(name),
              JsString(iso2Code)
            ) =>
          CountryKeyAndName(
            key = iso2Code,
            name = name
          )
        case err => deserializationError(s"Country expected $err")

    def write(c: CountryKeyAndName): JsValue = ???
