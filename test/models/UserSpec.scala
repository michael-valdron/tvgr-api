package models

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json

class UserSpec extends PlaySpec {

  "fromJson" should {
    "create entry from json" in {
      val json = Json.parse("{\"username\":\"user\",\"password\":\"password1\"}")
      val Some(result) = User.fromJson(json)
      result.username mustEqual "user"
      assert(result.password.isDefined)
      result.password.get mustEqual "password1"
    }

    "create entry from json without password" in {
      val json = Json.parse("{\"username\":\"user\"}")
      val Some(result) = User.fromJson(json)
      result.username mustEqual "user"
      assert(result.password.isEmpty)
    }
  }

  "toJson" should {
    "create json from single entry" in {
      val entry = User("user", Option("password1"))
      val expected = Json.parse("{\"username\":\"user\",\"password\":\"password1\"}")
      val result = User.toJson(entry)
      result mustEqual expected
    }

    "create json from single entry without password" in {
      val entry = User("user", None)
      val expected = Json.parse("{\"username\":\"user\"}")
      val result = User.toJson(entry)
      result mustEqual expected
    }
  }

  "apply" should {
    "create entry" in {
      val result = User.apply("user", Option("password1"))
      result.username mustEqual "user"
      assert(result.password.isDefined)
      result.password.get mustEqual "password1"
    }

    "create entry without password" in {
      val result = User.apply("user", None)
      result.username mustEqual "user"
      assert(result.password.isEmpty)
    }
  }

  "unapply" should {
    "create tuple from entry" in {
      val entry = User("user", Option("password1"))
      val Some(result) = User.unapply(entry)
      result._1 mustEqual "user"
      assert(result._2.isDefined)
      result._2.get mustEqual "password1"
    }

    "create tuple from entry without password" in {
      val entry = User("user", None)
      val Some(result) = User.unapply(entry)
      result._1 mustEqual "user"
      assert(result._2.isEmpty)
    }
  }
}
