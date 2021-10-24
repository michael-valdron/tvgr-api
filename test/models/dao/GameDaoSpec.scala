package models.dao

import fixtures.MyDataFixture
import models.Game
import models.tables.Games
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import slick.jdbc.PostgresProfile.api._

class GameDaoSpec extends PlaySpec
  with GuiceOneAppPerSuite with ScalaFutures with MyDataFixture {
  private val dao = fetchDao[GameDao](app)

  "testGet" should {
    "pull a record at id = 243425" in withSetupTeardown {
      val Some(result) = dao.get(243425).futureValue
      assert(result === testData(0))
    }
  }

  "testGetAll" should {
    "pull all records" in withSetupTeardown {
      val result = dao.getAll.futureValue
      assert((result diff testData) === Seq.empty)
    }
  }

  "testAdd" should {
    "add new record '123456' and return it" in withSetupTeardown {
      val entry = Game(123456, "A Simple Game", "Platformer",
        "A basic game for the basic gamer.", "2020-04-10")
      val Some(result) = dao.add(entry).futureValue
      assert(result.id === entry.id)
      assert(result.title === entry.title)
      assert(result.description === entry.description)
    }
  }

  "testEdit" should {
    "edit record '243425' and return it" in withSetupTeardown {
      val entry = testData.head
      val Some(result) = dao.edit(entry.copy(description = "A different description.")).futureValue
      result.id mustEqual entry.id
      assert(result.description != entry.description)
    }
  }

  "testDelete" should  {
    "delete record at id = '243425' and return it" in withSetupTeardown {
      val Some(result) = dao.delete(243425).futureValue
      assert(result.id === 243425)
    }
  }

  "testFilterById" should {
    "create filter by id query entity for finding record '243425' in 'games' relation" in withSetupTeardown {
      val db = loadDb
      val action = dao.filterById(243425)(TableQuery[Games])
      val result = db.run(action.result).futureValue
      assert(result.nonEmpty)
      assert(result.head.id === 243425)
    }
  }
}
