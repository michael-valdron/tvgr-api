package dao

import models.{VideoGameEntry, VideoGames}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class VideoGameDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {
  private val games = TableQuery[VideoGames]

  def filterById(entryId: Long)(query: Query[VideoGames, VideoGameEntry, Seq]): Query[VideoGames, VideoGameEntry, Seq] =
    query.filter(_.id === entryId)

  def get(entryId: Long): Future[Option[VideoGameEntry]] =
    db.run(filterById(entryId)(games).result.headOption)

  def getAll: Future[Seq[VideoGameEntry]] = db.run(games.result)

  def add(entry: VideoGameEntry): Future[Option[VideoGameEntry]] =
    db.run((games += entry) andThen filterById(entry.id)(games).result.headOption)

  def delete(entryId: Long): Future[Option[VideoGameEntry]] = {
    val q = filterById(entryId)(games)
    db.run(q.result.headOption zip q.delete).map(_._1)
  }
}
