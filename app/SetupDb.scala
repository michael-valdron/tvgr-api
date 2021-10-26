
import models.tables.{Games, Users}

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import slick.jdbc.PostgresProfile.api._

object SetupDb {
  protected def main(args: Array[String]): Unit = {
    val db = Database.forConfig("slick.dbs.default.db")
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
