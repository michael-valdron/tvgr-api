package models

import play.api.libs.json.{JsValue, Json, OFormat}

/**
 * Model entity for the sign in credentials of a `User`.
 *
 * @param username Unique identifier for the user
 * @param password The password to complete authentication of a user
 */
case class SignIn(username: String, password: String)

/**
 * Companion object of `SignIn`, full of util methods for the class including JSON conversions.
 *
 */
object SignIn {
  implicit private val jsonFormat: OFormat[SignIn] = Json.format[SignIn]

  /**
   * Converts a JSON object into an instance
   * of `SignIn`. Property names in the JSON
   * object must match the field names within
   * `SignIn` or None will be returned.
   *
   * @param json The JSON value to convert
   * @return The converted `SignIn` instance or None if invalid
   */
  def fromJson(json: JsValue): Option[SignIn] = json.validate.asOpt

  /**
   * Converts an instance of `SignIn` into
   * JSON object.
   *
   * @param cred An instance of `SignIn` to be converted
   * @return The converted JSON object entity
   */
  def toJson(cred: SignIn): JsValue = Json.toJson(cred)
}
