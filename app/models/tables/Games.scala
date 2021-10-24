package models.tables

import models.Game
import slick.jdbc.PostgresProfile.api._
import slick.lifted.ProvenShape

class Games(tag: Tag) extends Table[Game](tag, "games") {
  def id: Rep[Long] = column[Long]("id", O.PrimaryKey)
  def title: Rep[String] = column[String]("title")
  def genre: Rep[String] = column[String]("genre")
  def description: Rep[String] = column[String]("description")
  def releaseDate: Rep[String] = column[String]("release_date")
  def * : ProvenShape[Game] =
    (id, title, genre, description, releaseDate) <> ((Game.apply _).tupled, Game.unapply)
}
