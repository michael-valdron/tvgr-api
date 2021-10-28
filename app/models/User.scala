package models

import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.api.{Identity, LoginInfo}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.mohiva.play.silhouette.password.BCryptSha256PasswordHasher
import play.api.libs.json.{JsValue, Json, OFormat}

/**
 * Model of a record in the users relation.
 *
 * @param username Unique identifier for the user
 * @param password The password to complete authentication of a user (Optional)
 */
case class User(username: String, password: Option[String] = None) extends Identity {
  def loginInfo: LoginInfo = LoginInfo(CredentialsProvider.ID, username)
  def passwordInfo: PasswordInfo = PasswordInfo(BCryptSha256PasswordHasher.ID, password.get)
}

/**
 * Companion object of `User`, full of util methods for the class including JSON conversions.
 *
 */
object User {
  implicit private val jsonFormat: OFormat[User] = Json.format[User]

  /**
   * Converts a JSON object into an instance
   * of `User`. Property names in the JSON
   * object must match the field names within
   * `User` or None will be returned.
   *
   * @param json The JSON value to convert
   * @return The converted `User` instance or None if invalid
   */
  def fromJson(json: JsValue): Option[User] = json.validate.asOpt

  /**
   * Converts an instance of `User` into
   * JSON object.
   *
   * @param user An instance of `User` to be converted
   * @return The converted JSON object entity
   */
  def toJson(user: User): JsValue = Json.toJson(user)
}