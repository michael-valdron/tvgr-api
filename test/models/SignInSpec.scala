package models

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json

final class SignInSpec extends PlaySpec {

  "fromJson" should {
    "create entry from json" in {
      val json = Json.parse("{\"username\":\"user\",\"password\":\"password1\"}")
      val Some(result) = SignIn.fromJson(json)
      result.username mustEqual "user"
      result.password mustEqual "password1"
    }

    "try to create entry from json without password" in {
      val json = Json.parse("{\"username\":\"user\"}")
      val result = SignIn.fromJson(json)
      assert(result.isEmpty)
    }
  }

  "toJson" should {
    "create json from single entry" in {
      val entry = SignIn("user", "password1")
      val expected = Json.parse("{\"username\":\"user\",\"password\":\"password1\"}")
      val result = SignIn.toJson(entry)
      result mustEqual expected
    }
  }

  "apply" should {
    "create entry" in {
      val result = SignIn.apply("user", "password1")
      result.username mustEqual "user"
      result.password mustEqual "password1"
    }
  }

  "unapply" should {
    "create tuple from entry" in {
      val entry = SignIn("user", "password1")
      val Some(result) = SignIn.unapply(entry)
      result._1 mustEqual "user"
      result._2 mustEqual "password1"
    }
  }
}
