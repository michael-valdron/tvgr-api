package models.tables

import models.GameEntry
import slick.jdbc.PostgresProfile.api._
import slick.lifted.ProvenShape

class Games(tag: Tag) extends Table[GameEntry](tag, "games") {
  def id: Rep[Long] = column[Long]("id", O.PrimaryKey)
  def title: Rep[String] = column[String]("title")
  def genre: Rep[String] = column[String]("genre")
  def description: Rep[String] = column[String]("description")
  def releaseDate: Rep[String] = column[String]("release_date")
  def * : ProvenShape[GameEntry] =
    (id, title, genre, description, releaseDate) <> ((GameEntry.apply _).tupled, GameEntry.unapply)
}
