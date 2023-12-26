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
final case class CountrySubInfo(id: String, iso2code: String, value: String)
