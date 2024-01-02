package settings

object CountryApiSettings:
  val API_INDICATOR_URL: String  = "indicator";
  val API_BASIC_INFO_URL: String = "";
  val FROM_NET_KEY_TO_MAP_VALUE: Map[String, Map[String, String]] = Map(
    s"$API_BASIC_INFO_URL" ->
      Map("id" -> "basicInfo", "name" -> "Basic country info"),
    s"${API_INDICATOR_URL}/NY.GDP.MKTP.CD" ->
      Map("id" -> "GDP", "name" -> "GDP (current US$)"),
    s"$API_INDICATOR_URL/SP.POP.TOTL" ->
      Map("id" -> "totalPopulation", "name" -> "Total population"),
    s"$API_INDICATOR_URL/SE.ADT.1524.LT.ZS" ->
      Map("id" -> "literacyRate", "name" -> "Literacy rate, youth population"),
    s"$API_INDICATOR_URL/SI.POV.GINI" ->
      Map("id" -> "giniIndex", "name" -> "Gini index"),
    s"$API_INDICATOR_URL/EG.ELC.ACCS.ZS" ->
      Map("id" -> "eletricityAccess", "name" -> "Access to electricity")
  )

  def getIndicatorInfo(netId: String): Option[Map[String, String]] =
    FROM_NET_KEY_TO_MAP_VALUE.get(s"$API_INDICATOR_URL/$netId")

  val API_URL_ROOT: String    = "api.worldbank.org"
  val API_VERSION: String     = "v2"
  val API_COUNTRY_URL: String = s"$API_VERSION/country"
