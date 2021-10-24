package models.tables

import models.User
import slick.lifted.{ProvenShape, Tag}
import slick.jdbc.PostgresProfile.api._

class Users(tag: Tag) extends Table[User](tag, Some("play_silhouette"), "users") {
  def username: Rep[String] = column[String]("username", O.PrimaryKey, O.Unique)
  def password: Rep[Option[String]] = column[Option[String]]("password")
  def * : ProvenShape[User] = (username, password) <> ((User.apply _).tupled, User.unapply)
}
