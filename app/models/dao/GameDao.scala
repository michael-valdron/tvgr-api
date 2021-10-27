package models.dao

import models.Game
import models.tables.Games
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GameDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._

  def get(entryId: Long): Future[Option[Game]] =
    db.run(Games.filterById(entryId).result.headOption)

  def getAll: Future[Seq[Game]] = db.run(Games.result)

  def add(entry: Game): Future[Option[Game]] =
    db.run((Games += entry) andThen Games.filterById(entry.id).result.headOption)

  def edit(entry: Game): Future[Option[Game]] = {
    val q = Games.filterById(entry.id)
    db.run(q.update(entry) andThen q.result.headOption)
  }

  def delete(entryId: Long): Future[Option[Game]] = {
    val q = Games.filterById(entryId)
    db.run(q.result.headOption zip q.delete).map(_._1)
  }
}
