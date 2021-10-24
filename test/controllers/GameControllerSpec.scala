package controllers

import fixtures.MyDataFixture
import models.Game
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.mvc.Call

class GameControllerSpec() extends PlaySpec with MyDataFixture
  with GuiceOneServerPerSuite with ScalaFutures with IntegrationPatience {
  implicit private val ws: WSClient = app.injector.instanceOf(classOf[WSClient])

  "GET '/v1/games'" must {
    "return all records" in withSetupTeardown {
      val futureResult = wsCall(Call("GET", "/v1/games")).get()
      val status = futureResult.futureValue.status
      val result = Game.fromJsonArray(futureResult.futureValue.json)
      status mustEqual 200
      assert(result.nonEmpty)
      result.get mustEqual testData
    }
  }

  "GET '/v1/game/:id'" must {
    "return 404 not found with id = 1 (does not exist)" in withSetupTeardown {
      val futureResult = wsCall(Call("GET", "/v1/game/1")).get()
      val status = futureResult.futureValue.status
      status mustEqual 404
    }

    "returns an entry with id = '243425'" in withSetupTeardown {
      val entityId = testData.head.id
      val futureResult = wsCall(Call("GET", s"/v1/game/$entityId")).get()
      val status = futureResult.futureValue.status
      val result = Game.fromJson(futureResult.futureValue.json)
      status mustEqual 200
      assert(result.nonEmpty)
      result.get.id mustEqual entityId
    }
  }

  "PUT '/v1/game/add'" must {
    "add entry to database and return it" in withSetupTeardown {
      val entity = Game(45363, "Grim Conclusions", "Shooter", "", "2001-01-13")
      val futureResult = wsCall(Call("PUT", "/v1/game/add")).put(Game.toJson(entity))
      val status = futureResult.futureValue.status
      val result = Game.fromJson(futureResult.futureValue.json)
      status mustEqual 200
      assert(result.nonEmpty)
      result.get mustEqual entity
    }
  }

  "POST '/v1/edit/243425'" must {
    "edit entry in database and return it" in withSetupTeardown {
      val entity = testData.head
      val data = Game.toMap(entity.copy(description = "A different description.")) - "id"
      val futureResult = wsCall(Call("POST", s"/v1/edit/${entity.id}"))
        .post(Json.toJson(data))
      val status = futureResult.futureValue.status
      val result = Game.fromJson(futureResult.futureValue.json)
      assert(status == 200, s"expected status = 200, but got status = $status")
      assert(result.nonEmpty, s"result is empty")
      assert(result.get.id == entity.id, s"expected id = ${entity.id}, but got id = ${result.get.id}")
      assert(result.get.description != entity.description, "description field was not edited")
    }
  }

  "DELETE '/v1/game/243425'" must {
    "delete entry from database and return it" in withSetupTeardown {
      val entity = testData.head
      val futureResult = wsCall(Call("DELETE", s"/v1/game/${entity.id}")).delete()
      val status = futureResult.futureValue.status
      val result = Game.fromJson(futureResult.futureValue.json)
      status mustEqual 200
      assert(result.nonEmpty)
      result.get mustEqual entity
    }
  }
}
