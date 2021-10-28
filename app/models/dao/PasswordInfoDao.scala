package models.dao

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

/**
 * Data Access Object for fetching password information from users relation
 *
 * @param userDao Data Access Object for users relation to use with
 * @param classTag `PasswordInfo` class tag
 * @param ec Execution context for managing concurrency
 */
class PasswordInfoDao @Inject()(userDao: UserDao)(implicit val classTag: ClassTag[PasswordInfo], ec: ExecutionContext)
  extends DelegableAuthInfoDAO[PasswordInfo] {

  override def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]] =
    userDao.find(loginInfo).map(_.map(_.passwordInfo))

  override def add(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = update(loginInfo, authInfo)

  override def update(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] =
    userDao.find(loginInfo).flatMap {
      case Some(user) => userDao.update(user.copy(password = Some(authInfo.password))).map(_.passwordInfo)
      case None => Future.failed(new Exception("User does not exist."))
    }

  override def save(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = update(loginInfo, authInfo)

  override def remove(loginInfo: LoginInfo): Future[Unit] = update(loginInfo, PasswordInfo("", "")).map(_ => ())
}
