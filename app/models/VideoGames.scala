package models

import slick.jdbc.PostgresProfile.api._
import slick.lifted.ProvenShape

class VideoGames(tag: Tag) extends Table[VideoGameEntry](tag, "games") {
  def id: Rep[Long] = column[Long]("id", O.PrimaryKey)
  def title: Rep[String] = column[String]("title")
  def genre: Rep[String] = column[String]("genre")
  def description: Rep[String] = column[String]("description")
  def releaseDate: Rep[String] = column[String]("release_date")
  def * : ProvenShape[VideoGameEntry] =
    (id, title, genre, description, releaseDate) <> ((VideoGameEntry.apply _).tupled, VideoGameEntry.unapply)
}
