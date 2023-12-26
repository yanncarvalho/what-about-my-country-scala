package utils.extensions

import models.Country
import spray.json.{JsObject, JsString, JsValue}

object ExtensionCountry:
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
    //TODO melhorar código
    private def getFieldValue(key: String): String =
      countryJs.fields.get(key) match
        case Some(jsValue) =>
          if jsValue.isInstanceOf[JsObject] then
            jsValue.asJsObject.fields.getOrElse("value", JsString("")).toString
          else ""
        case None => ""