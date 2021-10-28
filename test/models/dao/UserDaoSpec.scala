package models.dao

import fixtures.DataFixture
import models.User
import org.scalatest.BeforeAndAfter
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.db.DBApi

final class UserDaoSpec extends PlaySpec with DataFixture
  with BeforeAndAfter with GuiceOneAppPerSuite with ScalaFutures {
  private lazy val dao: UserDao = Application.instanceCache[UserDao].apply(app)
  private lazy val dbApi: DBApi = app.injector.instanceOf[DBApi]

  before(initialize(dbApi))
  after(teardown(dbApi))

  "update" should {
    "modify existent user from database" in {
      val user = User("user", Some("password2"))
      val result = dao.update(user).futureValue
      assert(user.username == result.username)
      assert(result.password.isDefined)
    }
  }

  "save" should {
    "save non-existent user to database and return it" in {
      val user = User("user1", Some("password1"))
      val result = dao.save(user).futureValue
      assert(user.username == result.username)
      assert(result.password.isDefined)
    }
  }

  "find" should {
    "retrieve existent user from database" in {
      val user = User("user")
      val Some(result) = dao.find(user.loginInfo).futureValue
      assert(user.username == result.username)
      assert(result.password.isDefined)
    }

    "try to retrieve non-existent user from database and get None" in {
      val user = User("user1")
      val result = dao.find(user.loginInfo).futureValue
      assert(result.isEmpty)
    }
  }
}
