package handlers

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json

final class ErrorHandlerSpec extends PlaySpec {

  "createJson" should {
    "produce JsValue for a given request ID and message" in {
      val expected = Json.parse("{\"error\": {\"requestId\": \"10\", \"message\": \"Something went wrong.\"}}")
      expected mustEqual ErrorHandler.createJson("10", "Something went wrong.")
    }
  }
}
