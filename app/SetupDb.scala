
import com.typesafe.config.ConfigFactory
import models.tables.{Games, Users}

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import slick.basic.DatabaseConfig
import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._

object SetupDb {
  def main(args: Array[String]): Unit = {
    val db = DatabaseConfig.forConfig[PostgresProfile](
      sys.env("SLICK_TEST_DBPATH"),
      ConfigFactory.load().resolve()).db
    val users = TableQuery[Users]
    val games = TableQuery[Games]
    val setup = DBIO.seq(
      users.schema.create,
      games.schema.create
    )

    try Await.ready(db.run(setup), Duration.Inf)
    catch {
      case e: Exception => e.printStackTrace()
    }
    finally db.close()
  }
}
