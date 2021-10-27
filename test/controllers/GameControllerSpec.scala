package controllers

import models.Game
import models.dao.fixtures.DataFixture
import org.scalatest.BeforeAndAfter
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.db.DBApi
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.mvc.Call

final class GameControllerSpec() extends PlaySpec with DataFixture
  with BeforeAndAfter with GuiceOneServerPerSuite with ScalaFutures
  with IntegrationPatience {
  implicit private val ws: WSClient = app.injector.instanceOf(classOf[WSClient])
  private lazy val dbApi = app.injector.instanceOf[DBApi]

  before(initialize(dbApi))
  after(teardown(dbApi))

  "GET '/v1/games'" must {
    "return all records" in {
      val futureResult = wsCall(Call("GET", "/v1/games")).get()
      val status = futureResult.futureValue.status
      val expected = Json.parse("[{\"id\":243425,\"title\":\"Well of Quests\",\"genre\":\"RPG\"," +
        "\"description\":\"An adventure game of the ages!\",\"releaseDate\":\"2005-10-01\"},{\"id\":546324," +
        "\"title\":\"Racing 2020\",\"genre\":\"Racing\",\"description\":\"Race with the best cars of 2020.\"," +
        "\"releaseDate\":\"2020-11-12\"}]")
      val result = futureResult.futureValue.json
      status mustEqual 200
      result mustEqual expected
    }
  }

  "GET '/v1/game/:id'" must {
    "return 404 not found with id = 1 (does not exist)" in {
      val futureResult = wsCall(Call("GET", "/v1/game/1")).get()
      val status = futureResult.futureValue.status
      status mustEqual 404
    }

    "returns an entry with id = '243425'" in {
      val entityId = 243425
      val futureResult = wsCall(Call("GET", s"/v1/game/$entityId")).get()
      val status = futureResult.futureValue.status
      val result = Game.fromJson(futureResult.futureValue.json)
      status mustEqual 200
      assert(result.nonEmpty)
      result.get.id mustEqual entityId
    }
  }

  "PUT '/v1/game/add'" must {
    "add entry to database and return it" in {
      val newEntry = Json.parse("{\"id\":45363,\"title\":\"Grim Conclusions\",\"genre\":\"Shooter\"," +
        "\"description\":\"\",\"releaseDate\":\"2001-01-13\"}")
      val futureResult = wsCall(Call("PUT", "/v1/game/add")).put(newEntry)
      val status = futureResult.futureValue.status
      val result = futureResult.futureValue.json
      status mustEqual 200
      result mustEqual newEntry
    }
  }

  "POST '/v1/edit/243425'" must {
    "edit entry in database and return it" in {
      val entityId = 243425
      val entityDescription = "An adventure game of the ages!"
      val data = Json.parse("{\"title\":\"Well of Quests\",\"genre\":\"RPG\"," +
        "\"description\":\"A different description.\",\"releaseDate\":\"2005-10-01\"}")
      val futureResult = wsCall(Call("POST", s"/v1/edit/$entityId"))
        .post(data)
      val status = futureResult.futureValue.status
      val result = Game.fromJson(futureResult.futureValue.json)
      assert(status == 200, s"expected status = 200, but got status = $status")
      assert(result.nonEmpty, s"result is empty")
      assert(result.get.id == entityId, s"expected id = $entityId, but got id = ${result.get.id}")
      assert(result.get.description != entityDescription, "description field was not edited")
    }
  }

  "DELETE '/v1/game/243425'" must {
    "delete entry from database and return it" in {
      val entityId = 243425
      val futureResult = wsCall(Call("DELETE", s"/v1/game/$entityId")).delete()
      val status = futureResult.futureValue.status
      val expected = Json.parse(s"{\"id\":$entityId,\"title\":\"Well of Quests\",\"genre\":\"RPG\"," +
        "\"description\":\"An adventure game of the ages!\",\"releaseDate\":\"2005-10-01\"}")
      val result = futureResult.futureValue.json
      status mustEqual 200
      result mustEqual expected
    }
  }
}
