package models

import play.api.libs.json.{JsValue, Json, OFormat}

case class SignIn(username: String, password: String)

object SignIn {
  implicit private val jsonFormat: OFormat[SignIn] = Json.format[SignIn]

  def fromJson(json: JsValue): Option[SignIn] = json.validate.asOpt
  def toJson(cred: SignIn): JsValue = Json.toJson(cred)
}
