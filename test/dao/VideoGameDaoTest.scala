package dao

import fixtures.MyDataFixture
import models.{VideoGameEntry, VideoGames}
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.PlaySpec
import slick.jdbc.PostgresProfile.api._

class VideoGameDaoTest extends PlaySpec with ScalaFutures with MyDataFixture {
  private val app = buildApp
  private val dao = fetchDao[VideoGameDao](app)

  "testGet" should {
    "pull a record at id = 243425" in withSetupTeardown { _ =>
      val Some(result) = dao.get(243425).futureValue
      assert(result === testData(0))
    }
  }

  "testGetAll" should {
    "pull all records" in withSetupTeardown { _ =>
      val result = dao.getAll.futureValue
      assert((result diff testData) === Seq.empty)
    }
  }

  "testAdd" should {
    "add new record '123456' and return it" in withSetupTeardown {_ =>
      val entry = VideoGameEntry(123456, "A Simple Game", "Platformer",
        "A basic game for the basic gamer.", "2020-04-10")
      val Some(result) = dao.add(entry).futureValue
      assert(result.id === entry.id)
      assert(result.title === entry.title)
      assert(result.description === entry.description)
    }
  }

  "testDelete" should  {
    "delete record at id = '243425' and return it" in withSetupTeardown {_ =>
      val Some(result) = dao.delete(243425).futureValue
      assert(result.id === 243425)
    }
  }

  "testFilterById" should {
    "create filter by id query entity for finding record '243425' in 'games' relation" in withSetupTeardown {_ =>
      val db = Database.forConfig(dbPath)
      val action = dao.filterById(243425)(TableQuery[VideoGames])
      val result = db.run(action.result).futureValue
      assert(result.nonEmpty)
      assert(result.head.id === 243425)
    }
  }

}
