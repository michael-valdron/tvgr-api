package models

import play.api.libs.json.{JsValue, Json, OFormat}

import scala.reflect.classTag
import scala.reflect.runtime.currentMirror
import scala.reflect.runtime.universe.{TermSymbol, runtimeMirror, termNames, typeOf}

case class VideoGameEntry(id: Long, title: String, genre: String, description: String, releaseDate: String)

object VideoGameEntry {
  implicit private val jsonFormat: OFormat[VideoGameEntry] = Json.format[VideoGameEntry]

  def fromMap(map: Map[String, _]): VideoGameEntry = {
    val rm = runtimeMirror(classTag[VideoGameEntry].runtimeClass.getClassLoader)
    val entityClass = typeOf[VideoGameEntry].typeSymbol.asClass
    val clsm = rm.reflectClass(entityClass)
    val constructor = typeOf[VideoGameEntry].decl(termNames.CONSTRUCTOR).asMethod
    val consm = clsm.reflectConstructor(constructor)
    val args = constructor.paramLists.flatten.map { param =>
      val paramName = param.name.toString
      if (param.typeSignature <:< typeOf[Option[Any]])
        map.get(paramName)
      else
        map.getOrElse(paramName, throw new IllegalArgumentException(s"Map is missing required field '$paramName'."))
    }

    consm(args:_*).asInstanceOf[VideoGameEntry]
  }
  def toMap(entry: VideoGameEntry): Map[String, String] = {
    val r = currentMirror.reflect(entry)
    r.symbol.typeSignature.members.to(LazyList)
      .collect { case s if !s.isMethod => r.reflectField(s.asInstanceOf[TermSymbol]) }
      .map(f => f.symbol.name.toString.trim -> f.get.toString)
      .toMap
  }
  def fromJson(json: JsValue): Option[VideoGameEntry] = json.validate.asOpt
  def fromJsonArray(json: JsValue): Option[Seq[VideoGameEntry]] = json.validate[Seq[VideoGameEntry]].asOpt
  def toJson(entry: VideoGameEntry): JsValue = Json.toJson(entry)
  def toJson(entries: Seq[VideoGameEntry]): JsValue = Json.toJson(entries)
}
