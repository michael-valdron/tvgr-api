package controllers

import models.VideoGameEntry
import models.VideoGameEntry.toJson
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.libs.ws.WSClient
import play.api.mvc.Call

class VideoGameControllerSpec() extends PlaySpec with GuiceOneServerPerSuite
  with ScalaFutures with IntegrationPatience {
  implicit private val ws: WSClient = app.injector.instanceOf(classOf[WSClient])

  "GET '/v1/games'" must {
    "be empty body" in {
      val futureResult = wsCall(Call("GET", "/v1/games")).get()
      val status = futureResult.futureValue.status
      val result = VideoGameEntry.fromJsonArray(futureResult.futureValue.json)
      assert(status == 200)
      assert(result.nonEmpty && result.get === Seq.empty)
    }
  }

  "GET '/v1/game/1'" must {
    "be 404 not found" in {
      val futureResult = wsCall(Call("GET", "/v1/game/1")).get()
      val status = futureResult.futureValue.status
      assert(status == 404)
    }
  }

  "PUT '/v1/game/add'" must {
    "add entry to database and return it" in {
      val entity = VideoGameEntry(45363, "Halo", "Shooter", "", "2001-11-15")
      val futureResult = wsCall(Call("PUT", "/v1/game/add")).put(toJson(entity))
      val status = futureResult.futureValue.status
      val result = VideoGameEntry.fromJson(futureResult.futureValue.json)
      assert(status == 200)
      assert(result.nonEmpty && result.get == entity)
    }
  }

  "GET '/v1/game/45363'" must {
    "get entry" in {
      val entityId = 45363
      val futureResult = wsCall(Call("GET", s"/v1/game/$entityId")).get()
      val status = futureResult.futureValue.status
      val result = VideoGameEntry.fromJson(futureResult.futureValue.json)
      assert(status == 200)
      assert(result.nonEmpty && result.get.id == entityId)
    }
  }

  "DELETE '/v1/game/45363'" must {
    "delete entry from database and return it" in {
      val entity = VideoGameEntry(45363, "Halo", "Shooter", "", "2001-11-15")
      val futureResult = wsCall(Call("PUT", s"/v1/game/${entity.id}")).delete()
      val status = futureResult.futureValue.status
      val result = VideoGameEntry.fromJson(futureResult.futureValue.json)
      assert(status == 200)
      assert(result.nonEmpty && result.get == entity)
    }
  }
}
