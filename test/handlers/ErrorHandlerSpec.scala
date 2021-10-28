package handlers

import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.mvc.Call

final class ErrorHandlerSpec extends PlaySpec with GuiceOneServerPerSuite
  with ScalaFutures {

  "createJson" should {
    "produce JsValue for a given request ID and message" in {
      val expected = Json.parse("{\"error\": {\"requestId\": \"10\", \"message\": \"Something went wrong.\"}}")
      expected mustEqual ErrorHandler.createJson("10", "Something went wrong.")
    }
  }

  "onClientError" should {
    "produce generic 404 not found when accessing a non-existent route" in {
      implicit val ws: WSClient = app.injector.instanceOf(classOf[WSClient])
      val futureResult = wsCall(Call("GET", "/")).get()
      val status = futureResult.futureValue.status
      val contentType = futureResult.futureValue.contentType
      404 mustEqual status
      "application/json" mustEqual contentType
    }
  }
}
