package models.tables

import models.User
import org.scalatestplus.play.PlaySpec
import slick.jdbc.H2Profile.api._

final class UsersSpec extends PlaySpec {

  "filterByUsername" should {
    "create filter by username query entity for finding record 'user' in 'users' relation" in {
      val username = "user"
      val result = Users.filterByUsername(username)
      assert(result.isInstanceOf[Query[Users, User, Seq]])
    }
  }
}
