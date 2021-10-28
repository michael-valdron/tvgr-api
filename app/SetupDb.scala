import models.tables.{Games, Users}
import slick.dbio.DBIO
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.H2Profile.api._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
 * (For development mode only) Contains small script-like
 * method using slick query entities and database entry
 * to create relations in target database.
 *
 */
object SetupDb {

  /**
   * Script-like method using slick query entities and database entry
   * to create relations in target database. Requires 1 commandline argument
   * for specifying path to database config under `db.conf`.
   *
   * @param args Commandline arguments for process (1 Required)
   */
  def main(args: Array[String]): Unit = {
    if (args.length != 1) {
      println("usage: cmd <path.to.db.config>")
      return
    }
    val db = Database.forConfig(args.head)
    val actions = DBIO.seq(
      Users.schema.create,
      Games.schema.create
    )
    try Await.result(db.run(actions), Duration.Inf)
    catch {
      case e: Exception => println(e.getMessage)
    }
    finally db.close()
  }
}
