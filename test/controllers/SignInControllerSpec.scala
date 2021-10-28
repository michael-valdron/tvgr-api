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

final class SignInControllerSpec extends PlaySpec with DataFixture
  with BeforeAndAfter with GuiceOneServerPerSuite with ScalaFutures
  with IntegrationPatience {
  implicit private val ws: WSClient = app.injector.instanceOf(classOf[WSClient])
  private lazy val dbApi = app.injector.instanceOf[DBApi]

  before(initialize(dbApi))
  after(teardown(dbApi))

  "POST /login" must {
    "authenticate user and return access token under header 'X-Auth'" in {
      val data = Json.parse("{\"username\":\"user\",\"password\":\"password1\"}")
      val futureResult = wsCall(Call("POST", "/login")).post(data)
      val status = futureResult.futureValue.status
      val result = futureResult.futureValue.header("X-Auth")
      assert(200 == status, futureResult.futureValue.body)
      assert(result.isDefined)
    }

    "try to authenticate non-existent user and return 400 bad request" in {
      val data = Json.parse("{\"username\":\"user1\",\"password\":\"password2\"}")
      val futureResult = wsCall(Call("POST", "/login")).post(data)
      val status = futureResult.futureValue.status
      assert(400 == status, futureResult.futureValue.body)
    }

    "try to authenticate with invalid JSON and return 400 bad request" in {
      val data = Json.parse("{\"abc\":\"234\"}")
      val futureResult = wsCall(Call("POST", "/login")).post(data)
      val status = futureResult.futureValue.status
      assert(400 == status, futureResult.futureValue.body)
    }
  }
}
