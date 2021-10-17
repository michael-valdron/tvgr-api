package models

import slick.jdbc.PostgresProfile.api._

class VideoGames(tag: Tag) extends Table[VideoGameEntry](tag, "games") {
  def id = column[Long]("id", O.PrimaryKey)
  def title = column[String]("title")
  def genre = column[String]("genre")
  def description = column[String]("description")
  def releaseDate = column[String]("release_date")
  def * = (id, title, genre, description, releaseDate) <> ((VideoGameEntry.apply _).tupled, VideoGameEntry.unapply)
}
