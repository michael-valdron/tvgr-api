package models

import play.api.libs.json.{JsValue, Json, OFormat}

import scala.reflect.classTag
import scala.reflect.runtime.currentMirror
import scala.reflect.runtime.universe.{TermSymbol, runtimeMirror, termNames, typeOf}

/**
 * Model of a record in the games relation.
 *
 * @param id Unique identifier for the video game.
 * @param title The title of the video game.
 * @param genre The genre of the video game.
 * @param description The description of the video game.
 * @param releaseDate The release date (format: 2000-01-01) of the video game.
 */
case class Game(id: Long, title: String, genre: String, description: String, releaseDate: String)

/**
 * Companion object of `Game`, full of util methods for the class including JSON conversions.
 *
 */
object Game {
  implicit private val jsonFormat: OFormat[Game] = Json.format[Game]

  /**
   * Converts a `Map` data structure with `String` typed keys to
   * an instance of `Game`. The keys in the map must match the
   * field names in `Game` in order to convert.
   *
   * @throws IllegalArgumentException If there are missing fields
   * @param map A `Map` data structure to convert to a `Game` entity
   * @return The converted `Game` entity
   */
  def fromMap(map: Map[String, _]): Game = {
    val rm = runtimeMirror(classTag[Game].runtimeClass.getClassLoader)
    val entityClass = typeOf[Game].typeSymbol.asClass
    val clsm = rm.reflectClass(entityClass)
    val constructor = typeOf[Game].decl(termNames.CONSTRUCTOR).asMethod
    val consm = clsm.reflectConstructor(constructor)
    val args = constructor.paramLists.flatten.map { param =>
      val paramName = param.name.toString
      if (param.typeSignature <:< typeOf[Option[Any]])
        map.get(paramName)
      else
        map.getOrElse(paramName, throw new IllegalArgumentException(s"Map is missing required field '$paramName'."))
    }

    consm(args:_*).asInstanceOf[Game]
  }

  /**
   * Converts an instance of `Game` into a `Map` data
   * structure with `String` typed keys and values.
   *
   * @param entry An instance of `Game` to convert
   * @return The converted `Map` data structure
   */
  def toMap(entry: Game): Map[String, String] = {
    val r = currentMirror.reflect(entry)
    r.symbol.typeSignature.members.to(LazyList)
      .collect { case s if !s.isMethod => r.reflectField(s.asInstanceOf[TermSymbol]) }
      .map(f => f.symbol.name.toString.trim -> f.get.toString)
      .toMap
  }

  /**
   * Converts a JSON object into an instance
   * of `Game`. Property names in the JSON
   * object must match the field names within
   * `Game` or None will be returned.
   *
   * @param json The JSON value to convert
   * @return The converted `Game` instance or None if invalid
   */
  def fromJson(json: JsValue): Option[Game] = json.validate.asOpt

  /**
   * Converts a JSON array into an sequence
   * of `Game` instances. Property names in every
   * JSON object must match the field names within
   * `Game` or None will be returned.
   *
   * @param json The JSON value to convert
   * @return The converted array of `Game` instances or None if invalid
   */
  def fromJsonArray(json: JsValue): Option[Seq[Game]] = json.validate[Seq[Game]].asOpt

  /**
   * Converts an instance of `Game` into
   * JSON object.
   *
   * @param entry An instance of `Game` to be converted
   * @return The converted JSON object entity
   */
  def toJson(entry: Game): JsValue = Json.toJson(entry)

  /**
   * Converts a sequence of `Game` instances into
   * JSON array.
   *
   * @param entries A sequence of `Game` instances to be converted
   * @return The converted JSON array entity
   */
  def toJson(entries: Seq[Game]): JsValue = Json.toJson(entries)
}
