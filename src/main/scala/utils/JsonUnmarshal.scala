package utils

import models.{
  ApiResponse,
  BaseIndicatorInfo,
  Country,
  CountryKeyAndName,
  CountryKeyAndNameResponse,
  CountryResponse,
  Datum,
  Indicator,
  IndicatorResponse,
  Pagination,
  PaginationResponse
}
import utils.extensions.CountryExtension.{isValidCountry, sanitizeString}
import settings.CountryApiSettings as config
import spray.json.{
  DefaultJsonProtocol,
  DeserializationException,
  JsArray,
  JsNumber,
  JsObject,
  JsString,
  JsValue,
  JsonFormat,
  RootJsonFormat,
  deserializationError
}

object JsonUnmarshal extends DefaultJsonProtocol:

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
          if countriesFiltered.isEmpty then
            throw DeserializationException("No Countries found")
          else
            ApiResponse(
              pagination.convertTo[Pagination],
              countriesFiltered
            )
        case err => deserializationError(s"Api Response expected $err")

    def write(c: ApiResponse): JsValue = ???

  given RootJsonFormat[Pagination] with
    @SuppressWarnings(Array("match may not be exhaustive"))
    def read(json: JsValue): Pagination =
      json.asJsObject
        .getFields("page", "pages", "per_page", "total") match
        case Seq(
              JsNumber(page),
              JsNumber(pages),
              perPage,
              JsNumber(total)
            )
            if perPage.isInstanceOf[JsString] || perPage
              .isInstanceOf[JsNumber] =>
          Pagination(
            page.intValue,
            pages.intValue,
            perPage match
              case JsString(v) => v.toInt
              case v           => v.asInstanceOf[JsNumber].value.intValue
            ,
            total.intValue
          )

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
            iso2Code = iso2Code.sanitizeString,
            name = name.sanitizeString,
            capitalCity = capitalCity.sanitizeString,
            longitude = longitude,
            latitude = latitude,
            region = region("value").toString.sanitizeString,
            incomeLevel = incomeLevel("value").toString.sanitizeString
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
          if countriesFiltered.isEmpty then
            throw DeserializationException("No Countries found")
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
            key = iso2Code.sanitizeString,
            name = name.sanitizeString
          )
        case err => deserializationError(s"Country expected $err")

    def write(c: CountryKeyAndName): JsValue = ???

  given RootJsonFormat[Indicator] with
    def read(value: JsValue): Indicator =
      value match
        case JsArray(Vector(_, indicatorSeq)) =>
          val elements = indicatorSeq.asInstanceOf[JsArray].elements
          if elements.isEmpty then
            throw DeserializationException("No Indicators found")
          else
            val baseInfo = elements(0).convertTo[BaseIndicatorInfo]
            val data     = elements.map(_.convertTo[Datum])
            Indicator(baseInfo.id, baseInfo.description, data)

        case err => deserializationError(s"Api Response expected $err")

    def write(c: Indicator): JsValue = ???

given RootJsonFormat[Datum] with
  def read(value: JsValue): Datum =
    value.asJsObject.getFields("date", "value") match
      case Seq(
            JsString(date),
            JsNumber(value)
          ) =>
        Datum(date.toInt, value.toString)

      case err => deserializationError(s"Api Response expected $err")

  def write(c: Datum): JsValue = ???
given RootJsonFormat[BaseIndicatorInfo] with
  def read(value: JsValue): BaseIndicatorInfo =
    value.asJsObject.fields("indicator") match
      case JsObject(indicator) =>
        val id = indicator.getOrElse("id", "").asInstanceOf[JsString].value
        if id.isEmpty then
          throw DeserializationException("No basic indicator info found")
        else
          config.getIndicatorInfo(id) match
            case Some(info) => BaseIndicatorInfo(info("id"), info("name"))
            case _ =>
              throw DeserializationException("No basic indicator info found")
      case err => deserializationError(s"Api Response expected $err")

  def write(c: BaseIndicatorInfo): JsValue = ???
