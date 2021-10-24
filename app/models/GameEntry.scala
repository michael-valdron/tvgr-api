package models

import play.api.libs.json.{JsValue, Json, OFormat}

import scala.reflect.classTag
import scala.reflect.runtime.currentMirror
import scala.reflect.runtime.universe.{TermSymbol, runtimeMirror, termNames, typeOf}

case class GameEntry(id: Long, title: String, genre: String, description: String, releaseDate: String)

object GameEntry {
  implicit private val jsonFormat: OFormat[GameEntry] = Json.format[GameEntry]

  def fromMap(map: Map[String, _]): GameEntry = {
    val rm = runtimeMirror(classTag[GameEntry].runtimeClass.getClassLoader)
    val entityClass = typeOf[GameEntry].typeSymbol.asClass
    val clsm = rm.reflectClass(entityClass)
    val constructor = typeOf[GameEntry].decl(termNames.CONSTRUCTOR).asMethod
    val consm = clsm.reflectConstructor(constructor)
    val args = constructor.paramLists.flatten.map { param =>
      val paramName = param.name.toString
      if (param.typeSignature <:< typeOf[Option[Any]])
        map.get(paramName)
      else
        map.getOrElse(paramName, throw new IllegalArgumentException(s"Map is missing required field '$paramName'."))
    }

    consm(args:_*).asInstanceOf[GameEntry]
  }
  def toMap(entry: GameEntry): Map[String, String] = {
    val r = currentMirror.reflect(entry)
    r.symbol.typeSignature.members.to(LazyList)
      .collect { case s if !s.isMethod => r.reflectField(s.asInstanceOf[TermSymbol]) }
      .map(f => f.symbol.name.toString.trim -> f.get.toString)
      .toMap
  }
  def fromJson(json: JsValue): Option[GameEntry] = json.validate.asOpt
  def fromJsonArray(json: JsValue): Option[Seq[GameEntry]] = json.validate[Seq[GameEntry]].asOpt
  def toJson(entry: GameEntry): JsValue = Json.toJson(entry)
  def toJson(entries: Seq[GameEntry]): JsValue = Json.toJson(entries)
}
