package fixtures

import com.typesafe.config.ConfigFactory
import models.Game
import models.tables.Games
import slick.basic.DatabaseConfig
import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

trait MyDataFixture extends MyBaseFixture {
  protected val testData: Array[Game] = Array(
    Game(243425, "Well of Quests", "RPG", "An adventure game of the ages!", "2005-10-01"),
    Game(546324, "Racing 2020", "Racing", "Race with the best cars of 2020.", "2020-11-12")
  )

  protected def loadDb: Database =
    DatabaseConfig.forConfig[PostgresProfile](
      sys.env("SLICK_TEST_DBPATH"),
      config = ConfigFactory.load().resolve()).db

  protected def setup(): Unit = {
    val db = loadDb
    val games = TableQuery[Games]
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
    val db = loadDb
    val games = TableQuery[Games]
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
