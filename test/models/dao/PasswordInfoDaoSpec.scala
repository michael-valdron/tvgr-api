package models.dao

import fixtures.DataFixture
import models.User
import org.scalatest.BeforeAndAfter
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.db.DBApi

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

class PasswordInfoDaoSpec extends PlaySpec with DataFixture
  with BeforeAndAfter with GuiceOneAppPerSuite with ScalaFutures {
  private lazy val userDao: UserDao = Application.instanceCache[UserDao].apply(app)
  private lazy val passwordDao: PasswordInfoDao = new PasswordInfoDao(userDao)
  private lazy val dbApi: DBApi = app.injector.instanceOf[DBApi]

  before(initialize(dbApi))
  after(teardown(dbApi))

  "update" should {
    "updates the password for 'user'" in {
      val user = User("user", Some(hash("password1").password))
      val newPassword = hash("password2")
      val result = passwordDao.update(user.loginInfo, newPassword)
        .futureValue
      assert(result == newPassword)
      assert(result != user.passwordInfo)
    }

    "try to update the password for 'user1' which does not exist" in {
      val user = User("user1", Some(hash("password1").password))
      val newPassword = hash("password2")
      lazy val result = passwordDao.update(user.loginInfo, newPassword)
        .futureValue
      assertThrows[Exception](result)
    }
  }

  "remove" should {
    "removes new hashed password for 'user'" in {
      val user = User("user", Some(hash("password1").password))
      Await.ready(passwordDao.remove(user.loginInfo), Duration.Inf)
      succeed
    }
  }

  "add" should {
    "adds new hashed password for 'user'" in {
      val user = User("user", Some(hash("password1").password))
      val newPassword = hash("password2")
      val result = passwordDao.add(user.loginInfo, newPassword)
        .futureValue
      assert(result == newPassword)
      assert(result != user.passwordInfo)
    }
  }

  "save" should {
    "saves new hashed password for 'user'" in {
      val user = User("user", Some(hash("password1").password))
      val newPassword = hash("password2")
      val result = passwordDao.save(user.loginInfo, newPassword)
        .futureValue
      assert(result == newPassword)
      assert(result != user.passwordInfo)
    }
  }

  "find" should {
    "retrieve existent user's hashed password from database" in {
      val user = User("user")
      val result = passwordDao.find(user.loginInfo).futureValue
      assert(result.isDefined)
    }

    "try to retrieve non-existent user from database and get None" in {
      val user = User("user1")
      val result = passwordDao.find(user.loginInfo).futureValue
      assert(result.isEmpty)
    }
  }
}
