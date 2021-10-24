package controllers

import fixtures.MyDataFixture
import models.VideoGameEntry
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.libs.ws.WSClient
import play.api.mvc.Call

class VideoGameControllerSpec() extends PlaySpec with MyDataFixture
  with GuiceOneServerPerSuite with ScalaFutures with IntegrationPatience {
  implicit private val ws: WSClient = app.injector.instanceOf(classOf[WSClient])

  "GET '/v1/games'" must {
    "return all records" in withSetupTeardown {
      val futureResult = wsCall(Call("GET", "/v1/games")).get()
      val status = futureResult.futureValue.status
      val result = VideoGameEntry.fromJsonArray(futureResult.futureValue.json)
      status mustEqual 200
      assert(result.nonEmpty)
      result.get mustEqual testData
    }
  }

  "GET '/v1/game/1'" must {
    "return 404 not found" in withSetupTeardown {
      val futureResult = wsCall(Call("GET", "/v1/game/1")).get()
      val status = futureResult.futureValue.status
      status mustEqual 404
    }
  }

  "PUT '/v1/game/add'" must {
    "add entry to database and return it" in withSetupTeardown {
      val entity = VideoGameEntry(45363, "Grim Conclusions", "Shooter", "", "2001-01-13")
      val futureResult = wsCall(Call("PUT", "/v1/game/add")).put(VideoGameEntry.toJson(entity))
      val status = futureResult.futureValue.status
      val result = VideoGameEntry.fromJson(futureResult.futureValue.json)
      status mustEqual 200
      assert(result.nonEmpty)
      result.get mustEqual entity
    }
  }

  "GET '/v1/game/'" must {
    "returns an entry with id = '243425'" in withSetupTeardown {
      val entityId = testData.head.id
      val futureResult = wsCall(Call("GET", s"/v1/game/$entityId")).get()
      val status = futureResult.futureValue.status
      val result = VideoGameEntry.fromJson(futureResult.futureValue.json)
      status mustEqual 200
      assert(result.nonEmpty)
      result.get.id mustEqual entityId
    }
  }

  "DELETE '/v1/game/243425'" must {
    "delete entry from database and return it" in withSetupTeardown {
      val entity = testData.head
      val futureResult = wsCall(Call("DELETE", s"/v1/game/${entity.id}")).delete()
      val status = futureResult.futureValue.status
      val result = VideoGameEntry.fromJson(futureResult.futureValue.json)
      status mustEqual 200
      assert(result.nonEmpty)
      result.get mustEqual entity
    }
  }
}
