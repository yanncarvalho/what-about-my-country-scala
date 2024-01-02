package models

final case class ApiResponse(pagination: Pagination, countries: Seq[Country])

final case class Pagination(
    page: Int,
    pages: Int,
    perPage: Int,
    total: Int
)

final case class PaginationResponse(pagination: Pagination)

final case class CountryResponse(countries: Seq[Country])

final case class CountryKeyAndName(key: String, name: String)

final case class CountryKeyAndNameResponse(countries: Seq[CountryKeyAndName])

final case class Country(
    iso2Code: String,
    name: String,
    capitalCity: String,
    longitude: String,
    latitude: String,
    region: String,
    incomeLevel: String
)

final case class Datum(year: Int, value: String)
final case class Indicator(id: String, description: String, data: Seq[Datum])

final case class IndicatorResponse(indicator: Indicator)

final case class BaseIndicatorInfo(id: String, description : String)