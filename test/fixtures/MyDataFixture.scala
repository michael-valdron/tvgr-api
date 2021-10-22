package fixtures

import models.{VideoGameEntry, VideoGames}
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

trait MyDataFixture extends MyBaseFixture {
  protected val dbPath: String = "slick.dbs.default.db"
  protected val testData: Array[VideoGameEntry] = Array(
    VideoGameEntry(243425, "Well of Quests", "RPG", "An adventure game of the ages!", "2005-10-01"),
    VideoGameEntry(546324, "Racing 2020", "Racing", "Race with the best cars of 2020.", "2020-11-12")
  )

  protected def setup(): Unit = {
    val db = Database.forConfig(dbPath)
    val games = TableQuery[VideoGames]
    val setup = DBIO.seq(
      games.schema.create,
      games ++= testData
    )

    try Await.ready(db.run(setup), Duration.Inf)
    catch {
      case e: Exception => e.printStackTrace()
    }
    finally db.close()
  }

  protected def teardown(): Unit = {
    val db = Database.forConfig("slick.dbs.default.db")
    val games = TableQuery[VideoGames]
    val setup = DBIO.seq(
      games.delete,
      games.schema.drop
    )

    try Await.ready(db.run(setup), Duration.Inf)
    catch {
      case e: Exception => e.printStackTrace()
    }
    finally db.close()
  }
}
