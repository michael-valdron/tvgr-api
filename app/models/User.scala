package models

import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.api.{Identity, LoginInfo}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.mohiva.play.silhouette.password.BCryptSha256PasswordHasher
import play.api.libs.json.{JsValue, Json, OFormat}

case class User(username: String, password: Option[String] = None) extends Identity {
  def loginInfo: LoginInfo = LoginInfo(CredentialsProvider.ID, username)
  def passwordInfo: PasswordInfo = PasswordInfo(BCryptSha256PasswordHasher.ID, password.get)
}

object User {
  implicit private val jsonFormat: OFormat[User] = Json.format[User]
  def fromJson(json: JsValue): Option[User] = json.validate.asOpt
  def toJson(user: User): JsValue = Json.toJson(user)
}