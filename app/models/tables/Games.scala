package models.tables

import models.Game
import slick.jdbc.H2Profile.api._
import slick.lifted.ProvenShape

/**
 * Table Model of the games relation.
 *
 * @param tag The table tag
 */
class Games(tag: Tag) extends Table[Game](tag, "games") {
  def id: Rep[Long] = column[Long]("id", O.PrimaryKey)
  def title: Rep[String] = column[String]("title")
  def genre: Rep[String] = column[String]("genre")
  def description: Rep[String] = column[String]("description")
  def releaseDate: Rep[String] = column[String]("release_date")
  def * : ProvenShape[Game] =
    (id, title, genre, description, releaseDate) <> ((Game.apply _).tupled, Game.unapply)
}

/**
 * Companion object `Games` for creating a `TableQuery` and
 * creating predefined queries for querying the games relation.
 *
 */
object Games extends TableQuery[Games](new Games(_)) {

  /**
   * Creates query for filtering `Game` records by their id.
   *
   * @param entryId The record id to find
   * @return An instance of `Query` to perform the aforementioned query against the database
   */
  def filterById(entryId: Long): Query[Games, Game, Seq] =
    this.filter(_.id === entryId)
}