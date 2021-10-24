package models.services

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import models.User
import models.dao.UserDao

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UserService @Inject()(userDao: UserDao)(implicit ec: ExecutionContext) extends IdentityService[User] {
  def retrieve(loginInfo: LoginInfo): Future[Option[User]] = userDao.find(loginInfo)
  def save(user: User): Future[User] = userDao.save(user)
  def update(user: User): Future[User] = userDao.update(user)
}
