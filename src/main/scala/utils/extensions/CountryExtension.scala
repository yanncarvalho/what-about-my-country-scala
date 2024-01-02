package utils.extensions

import models.Country
import spray.json.{JsObject, JsString, JsValue}

object CountryExtension:
  extension (countryJs: JsObject)
    def isValidCountry: Boolean =

      val baseFields = countryJs
        .getFields(
          "iso2Code",
          "name",
          "capitalCity",
          "longitude",
          "latitude"
        )
      val isNonEmpty =
        baseFields.forall(_.asInstanceOf[JsString].value.nonEmpty)

      val isValidAuxField = (value: String) =>
        !value.isBlank && !value.contentEquals("Aggregates")

      baseFields.size == 5 && isNonEmpty && isValidAuxField(
        getFieldValue("region")
      ) && isValidAuxField(
        getFieldValue("incomeLevel")
      )
    // TODO melhorar código
    private def getFieldValue(key: String): String =
      countryJs.fields.get(key) match
        case Some(jsValue) =>
          if jsValue.isInstanceOf[JsObject] then
            jsValue.asJsObject.fields.getOrElse("value", JsString("")).toString
          else ""
        case None => ""

  extension (str: String)
    def sanitizeString: String =

      val rule: (String => String) = s => s.replaceAll("\\s*[SAR]*\\s*,.*", "")
      val exceptions: Map[String, String] = Map(
        "Congo, Dem. Rep." -> "Democratic Republic of the Congo",
        "Congo, Rep."      -> "Republic of the Congo",
        "Korea, Dem. People's Rep." -> "Democratic People's Republic of Korea (North Korea)",
        "Korea, Rep."       -> "Republic of Korea (South Korea)",
        "Bahamas, The"      -> "The Bahamas",
        "Population, total" -> "Total population",
        "Lao PDR"           -> "Lao People's Democratic Republic (Laos)",
        "St. Vincent and the Grenadines" -> "Saint Vincent and the Grenadines",
        "St. Lucia"                      -> "Saint Lucia",
        "St. Kitts and Nevis"            -> "Saint Kitts and Nevis"
      )
      val strippedStr: String = str.strip()
      exceptions.getOrElse(strippedStr, rule(strippedStr))
