package models.dao

import com.mohiva.play.silhouette.api.LoginInfo
import models.User
import models.tables.Users
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

/**
 * Data Access Object for users relation.
 *
 * @param dbConfigProvider Database configuration provider
 * @param ec Execution context for managing concurrency
 */
class UserDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._

  /**
   * Finds `User` entity using the login information. Returns None if not found.
   *
   * @param loginInfo Login information
   * @return `User` entity from database
   */
  def find(loginInfo: LoginInfo): Future[Option[User]] =
    db.run(Users.filterByUsername(loginInfo.providerKey).result.headOption)

  /**
   * Saves new `User` entity to database.
   *
   * @param user New `User` entity
   * @return Added `User` entity
   */
  def save(user: User): Future[User] = db.run(Users returning Users += user)

  /**
   * Updates user in database.
   *
   * @param user Modified `User` entity
   * @return Updated `User` entity
   */
  def update(user: User): Future[User] =
    db.run(Users.filterByUsername(user.username).update(user).map(_ => user))

}
