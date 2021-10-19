package models

import play.api.libs.json.{JsValue, Json, OFormat}

case class VideoGameEntry(id: Long, title: String, genre: String, description: String, releaseDate: String)

object VideoGameEntry {
  implicit private val jsonFormat: OFormat[VideoGameEntry] = Json.format[VideoGameEntry]

  def fromJson(json: JsValue): Option[VideoGameEntry] = json.validate.asOpt
  def fromJsonArray(json: JsValue): Option[Seq[VideoGameEntry]] = json.validate[Seq[VideoGameEntry]].asOpt
  def toJson(entry: VideoGameEntry): JsValue = Json.toJson(entry)
  def toJson(entries: Seq[VideoGameEntry]): JsValue = Json.toJson(entries)
}
