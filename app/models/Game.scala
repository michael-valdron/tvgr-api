package models

import play.api.libs.json.{JsValue, Json, OFormat}

import scala.reflect.classTag
import scala.reflect.runtime.currentMirror
import scala.reflect.runtime.universe.{TermSymbol, runtimeMirror, termNames, typeOf}

case class Game(id: Long, title: String, genre: String, description: String, releaseDate: String)

object Game {
  implicit private val jsonFormat: OFormat[Game] = Json.format[Game]

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
  def toMap(entry: Game): Map[String, String] = {
    val r = currentMirror.reflect(entry)
    r.symbol.typeSignature.members.to(LazyList)
      .collect { case s if !s.isMethod => r.reflectField(s.asInstanceOf[TermSymbol]) }
      .map(f => f.symbol.name.toString.trim -> f.get.toString)
      .toMap
  }
  def fromJson(json: JsValue): Option[Game] = json.validate.asOpt
  def fromJsonArray(json: JsValue): Option[Seq[Game]] = json.validate[Seq[Game]].asOpt
  def toJson(entry: Game): JsValue = Json.toJson(entry)
  def toJson(entries: Seq[Game]): JsValue = Json.toJson(entries)
}
