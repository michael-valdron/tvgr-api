import models.VideoGames
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Setup extends App {
  val db = Database.forConfig("slick.dbs.default.db")
  val games = TableQuery[VideoGames]
  val setup = DBIO.seq(games.schema.create)

  try Await.ready(db.run(setup), Duration.Inf)
  catch {
    case e: Exception => e.printStackTrace()
  }
  finally db.close()
}
