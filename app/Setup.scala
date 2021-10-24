
import models.tables.Games

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import slick.jdbc.PostgresProfile.api._

object Setup {
  def main(args: Array[String]): Unit = {
    val db = Database.forConfig("slick.dbs.default.db")
    val games = TableQuery[Games]
    val setup = DBIO.seq(games.schema.create)

    try Await.ready(db.run(setup), Duration.Inf)
    catch {
      case e: Exception => e.printStackTrace()
    }
    finally db.close()
  }
}
