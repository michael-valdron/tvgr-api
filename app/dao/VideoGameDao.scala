package dao

import models.{VideoGameEntry, VideoGames}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class VideoGameDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {
  implicit private val games: TableQuery[VideoGames] = TableQuery[VideoGames]

  def filterById(entryId: Long)(
    implicit query: Query[VideoGames, VideoGameEntry, Seq]): Query[VideoGames, VideoGameEntry, Seq] =
    query.filter(_.id === entryId)

  def get(entryId: Long): Future[Option[VideoGameEntry]] =
    db.run(filterById(entryId).result.headOption)

  def getAll: Future[Seq[VideoGameEntry]] = db.run(games.result)

  def add(entry: VideoGameEntry): Future[Option[VideoGameEntry]] =
    db.run((games += entry) andThen filterById(entry.id).result.headOption)

  def edit(entry: VideoGameEntry): Future[Option[VideoGameEntry]] = {
    val q = filterById(entry.id)
    db.run(q.update(entry) andThen q.result.headOption)
  }

  def delete(entryId: Long): Future[Option[VideoGameEntry]] = {
    val q = filterById(entryId)
    db.run(q.result.headOption zip q.delete).map(_._1)
  }
}
