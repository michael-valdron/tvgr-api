package models.dao

import com.mohiva.play.silhouette.api.LoginInfo
import models.User
import models.tables.Users
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UserDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._

  def find(loginInfo: LoginInfo): Future[Option[User]] =
    db.run(Users.filterByUsername(loginInfo.providerKey).result.headOption)

  def save(user: User): Future[User] = db.run(Users returning Users += user)

  def update(user: User): Future[User] =
    db.run(Users.filterByUsername(user.username).update(user).map(_ => user))

}
