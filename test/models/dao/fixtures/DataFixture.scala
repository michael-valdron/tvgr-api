package models.dao.fixtures

import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import com.mohiva.play.silhouette.password.{BCryptPasswordHasher, BCryptSha256PasswordHasher}
import models.dao.fixtures.DataFixture.hash
import models.tables.{Games, Users}
import models.{Game, User}
import play.api.db.DBApi
import play.api.db.evolutions.Evolutions
import slick.dbio.DBIO
import slick.jdbc.H2Profile.api._
import slick.jdbc.JdbcBackend.Database

import scala.concurrent.Await
import scala.concurrent.duration.Duration

trait DataFixture extends {
  private val configName: String = "default"
  private val configPath: String = s"slick.dbs.$configName.db"

  protected val testUsersData: Array[User] = Array(
    User("user", hash("password1"))
  )
  protected val testGamesData: Seq[Game] = Seq(
    Game(243425, "Well of Quests", "RPG", "An adventure game of the ages!", "2005-10-01"),
    Game(546324, "Racing 2020", "Racing", "Race with the best cars of 2020.", "2020-11-12")
  )

  protected def initialize(dbApi: DBApi): Unit = {
    val db = Database.forConfig(configPath)
    val actions = DBIO.seq(
      Users ++= testUsersData,
      Games ++= testGamesData
    )
    Evolutions.applyEvolutions(dbApi.database(configName))
    try Await.result(db.run(actions), Duration.Inf)
    finally db.close()
  }

  protected def teardown(dbApi: DBApi): Unit = {
    Evolutions.cleanupEvolutions(dbApi.database(configName))
  }
}

object DataFixture {
  private def hash(password: String): Option[String] =
    Some(
      PasswordHasherRegistry(new BCryptSha256PasswordHasher(), Seq(new BCryptPasswordHasher()))
        .current.hash(password).password
    )
}
