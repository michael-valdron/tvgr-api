package models

import play.api.libs.json.Json

case class VideoGameEntry(id: Long, title: String, genre: String, description: String, releaseDate: String)

object VideoGameEntry {
  implicit val jsonFormat = Json.format[VideoGameEntry]
}
