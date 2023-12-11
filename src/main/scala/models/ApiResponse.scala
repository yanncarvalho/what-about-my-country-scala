package models

final case class ApiResponse(pagination: Pagination, country: List[Country])

final case class Pagination(
    page: Int,
    pages: Int,
    perPage: Int,
    total: Int
)
final case class CountrySubInfo(id: String, iso2code: String, value: String)

final case class Country(
    id: String,
    iso2Code: String,
    name: String,
    capitalCity: String,
    longitude: String,
    latitude: String,
    region: CountrySubInfo,
    adminRegion: CountrySubInfo,
    incomeLevel: CountrySubInfo,
    lendingType: CountrySubInfo
)
