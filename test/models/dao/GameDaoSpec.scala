package models.dao

import models.Game
import models.tables.Games
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.db.DBApi
import play.api.db.evolutions.Evolutions
import slick.jdbc.PostgresProfile.api._

final class GameDaoSpec extends PlaySpec
  with GuiceOneAppPerSuite with ScalaFutures with BeforeAndAfterAll {
  private lazy val dao = Application.instanceCache[GameDao].apply(app)
  private lazy val dbApi = app.injector.instanceOf[DBApi]

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    Evolutions.applyEvolutions(dbApi.database("default"))
  }

  override protected def afterAll(): Unit = {
    Evolutions.cleanupEvolutions(dbApi.database("default"))
    super.afterAll()
  }

  "testGet" should {
    "pull a record at id = 243425" in {
      val Some(result) = dao.get(243425).futureValue
      result.id mustEqual 243425
      result.title mustEqual "Well of Quests"
      result.genre mustEqual "RPG"
      result.description mustEqual "An adventure game of the ages!"
      result.releaseDate mustEqual "2005-10-01"
    }
  }

  "testGetAll" should {
    "pull all records" in {
      val expected = Array(
        Game(243425, "Well of Quests", "RPG", "An adventure game of the ages!", "2005-10-01"),
        Game(546324, "Racing 2020", "Racing", "Race with the best cars of 2020.", "2020-11-12")
      )
      val result = dao.getAll.futureValue
      result.length mustEqual expected.length
      result.head.id mustEqual expected.head.id
      result(1).title mustEqual expected(1).title
    }
  }

  "testAdd" should {
    "add new record '123456' and return it" in {
      val entry = Game(123456, "A Simple Game", "Platformer",
        "A basic game for the basic gamer.", "2020-04-10")
      val Some(result) = dao.add(entry).futureValue
      assert(result.id === entry.id)
      assert(result.title === entry.title)
      assert(result.description === entry.description)
    }
  }

  "testEdit" should {
    "edit record '243425' and return it" in {
      val entry = Game(243425, "Well of Quests", "RPG", "An adventure game of the ages!", "2005-10-01")
      val Some(result) = dao.edit(entry.copy(description = "A different description.")).futureValue
      result.id mustEqual entry.id
      assert(result.description != entry.description)
    }
  }

  "testDelete" should  {
    "delete record at id = '243425' and return it" in {
      val Some(result) = dao.delete(243425).futureValue
      assert(result.id === 243425)
    }
  }

  "testFilterById" should {
    "create filter by id query entity for finding record '243425' in 'games' relation" in {
      val entryId = 243425
      val result = dao.filterById(entryId)(TableQuery[Games])
      println(result.toString())
      assert(result.isInstanceOf[Query[Games, Game, Seq]])
      //assert()
    }
  }
}
