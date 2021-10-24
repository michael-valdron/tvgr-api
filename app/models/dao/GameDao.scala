package models.dao

import models.Game
import models.tables.Games
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GameDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {
  implicit private val games: TableQuery[Games] = TableQuery[Games]

  def filterById(entryId: Long)(
    implicit query: Query[Games, Game, Seq]): Query[Games, Game, Seq] =
    query.filter(_.id === entryId)

  def get(entryId: Long): Future[Option[Game]] =
    db.run(filterById(entryId).result.headOption)

  def getAll: Future[Seq[Game]] = db.run(games.result)

  def add(entry: Game): Future[Option[Game]] =
    db.run((games += entry) andThen filterById(entry.id).result.headOption)

  def edit(entry: Game): Future[Option[Game]] = {
    val q = filterById(entry.id)
    db.run(q.update(entry) andThen q.result.headOption)
  }

  def delete(entryId: Long): Future[Option[Game]] = {
    val q = filterById(entryId)
    db.run(q.result.headOption zip q.delete).map(_._1)
  }
}
