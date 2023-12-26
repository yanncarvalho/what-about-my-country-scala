package utils

import models.{ApiResponse, Country, CountryKeyAndName, CountryKeyAndNameResponse, CountryResponse, CountrySubInfo, Pagination, PaginationResponse}
import utils.extensions.ExtensionCountry.isValidCountry
import spray.json.{DefaultJsonProtocol, DeserializationException, JsArray, JsObject, JsNumber, JsString, JsValue, JsonFormat, RootJsonFormat, deserializationError}

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
          val countriesFiltered = countries
            .asInstanceOf[JsArray]
            .elements
            .withFilter(_.asJsObject.isValidCountry)
            .map(_.convertTo[Country])
          if countriesFiltered.isEmpty then throw DeserializationException("No Countries found")
          else
            ApiResponse(
              pagination.convertTo[Pagination],
              countriesFiltered
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
          "iso2Code",
          "name",
          "capitalCity",
          "longitude",
          "latitude",
          "region",
          "incomeLevel"
        ) match

        case Seq(
              JsString(iso2Code),
              JsString(name),
              JsString(capitalCity),
              JsString(longitude),
              JsString(latitude),
              JsObject(region),
              JsObject(incomeLevel)
            ) =>
          Country(
            iso2Code = iso2Code,
            name = name,
            capitalCity = capitalCity,
            longitude = longitude,
            latitude = latitude,
            region = region("value").toString,
            incomeLevel = incomeLevel("value").toString
          )
        case err => deserializationError(s"Country expected $err")

    def write(c: Country): JsValue = ???

  given RootJsonFormat[CountryKeyAndNameResponse] with

    def read(value: JsValue): CountryKeyAndNameResponse =
      value match
        case JsArray(Vector(_, countries)) =>
          val countriesFiltered = countries
            .asInstanceOf[JsArray]
            .elements
            .withFilter(_.asJsObject.isValidCountry)
            .map(_.convertTo[CountryKeyAndName])
          if countriesFiltered.isEmpty then throw DeserializationException("No Countries found")
          else CountryKeyAndNameResponse(countriesFiltered)

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
