package models.services

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import models.User
import models.dao.UserDao

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

/**
 * Service for the `User` identity.
 *
 * @param userDao Data access object for users relation
 * @param ec Execution context for managing concurrency
 */
class UserService @Inject()(userDao: UserDao)(implicit ec: ExecutionContext) extends IdentityService[User] {

  /**
   * Fetches `User` entity using the login information. Returns None if non-existent.
   *
   * @param loginInfo Login information
   * @return `User` entity from database
   */
  def retrieve(loginInfo: LoginInfo): Future[Option[User]] = userDao.find(loginInfo)

  /**
   * Saves new `User` entity to database.
   *
   * @param user New `User` entity
   * @return Added `User` entity
   */
  def save(user: User): Future[User] = userDao.save(user)

  /**
   * Updates user in database.
   *
   * @param user Modified `User` entity
   * @return Updated `User` entity
   */
  def update(user: User): Future[User] = userDao.update(user)
}
