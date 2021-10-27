package models.tables

import models.User
import slick.lifted.{ProvenShape, Tag}
import slick.jdbc.H2Profile.api._

class Users(tag: Tag) extends Table[User](tag, "users") {
  def username: Rep[String] = column[String]("username", O.PrimaryKey, O.Unique)
  def password: Rep[Option[String]] = column[Option[String]]("password")
  def * : ProvenShape[User] = (username, password) <> ((User.apply _).tupled, User.unapply)
}

object Users extends TableQuery[Users](new Users(_)) {
  def filterByUsername(username: String): Query[Users, User, Seq] =
    this.filter(_.username === username)
}
