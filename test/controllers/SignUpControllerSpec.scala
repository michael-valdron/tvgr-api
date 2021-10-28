package controllers

import fixtures.DataFixture
import org.scalatest.BeforeAndAfter
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.db.DBApi
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.mvc.Call

final class SignUpControllerSpec extends PlaySpec with DataFixture
  with BeforeAndAfter with GuiceOneServerPerSuite with ScalaFutures
  with IntegrationPatience {
  implicit private val ws: WSClient = app.injector.instanceOf(classOf[WSClient])
  private lazy val dbApi = app.injector.instanceOf[DBApi]

  before(initialize(dbApi))
  after(teardown(dbApi))

  "POST /register" must {
    "register new user and return JSON with username back" in {
      val data = Json.parse("{\"username\":\"user1\",\"password\":\"password2\"}")
      val expected = Json.parse("{\"username\":\"user1\"}")
      val futureResult = wsCall(Call("POST", "/register")).post(data)
      val status = futureResult.futureValue.status
      val result = futureResult.futureValue.json
      assert(200 == status, futureResult.futureValue.body)
      assert(expected == result)
    }

    "try to register existent user and return 409 conflict" in {
      val data = Json.parse("{\"username\":\"user\",\"password\":\"password1\"}")
      val futureResult = wsCall(Call("POST", "/register")).post(data)
      val status = futureResult.futureValue.status
      assert(409 == status, futureResult.futureValue.body)
    }

    "try to register with invalid JSON and return 400 bad request" in {
      val data = Json.parse("{\"abc\":\"234\"}")
      val futureResult = wsCall(Call("POST", "/register")).post(data)
      val status = futureResult.futureValue.status
      assert(400 == status, futureResult.futureValue.body)
    }
  }
}
