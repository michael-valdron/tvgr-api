package models.dao

import com.mohiva.play.silhouette.api.LoginInfo
import models.User
import models.tables.Users
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UserDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends  HasDatabaseConfigProvider[JdbcProfile] {
  implicit private val users: TableQuery[Users] = TableQuery[Users]

  def filterByUsername(username: String)(implicit query: Query[Users, User, Seq]): Query[Users, User, Seq] =
    query.filter(_.username === username)

  def find(loginInfo: LoginInfo): Future[Option[User]] =
    db.run(filterByUsername(loginInfo.providerKey).result.headOption)

  def save(user: User): Future[User] = db.run(users returning users += user)

  def update(user: User): Future[User] =
    db.run(filterByUsername(user.username).update(user).map(_ => user))

}
