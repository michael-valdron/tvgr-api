package services

import models.{VideoGameEntry, VideoGames}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class VideoGameDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {
  private val games = TableQuery[VideoGames]

  def get(entryId: Long): Future[Option[VideoGameEntry]] = {
    val q = games.filter(_.id === entryId)
    val action = q.result.headOption
    db.run(action)
  }

  def getAll: Future[Seq[VideoGameEntry]] = {
    val action = games.result
    db.run(action)
  }

  def add(entry: VideoGameEntry): Future[Option[VideoGameEntry]] = {
    val q = games.filter(_.id === entry.id)
    val action = (games += entry) andThen q.result.headOption
    db.run(action)
  }

  def delete(entryId: Long): Future[Option[VideoGameEntry]] = {
    val q = games.filter(_.id === entryId)
    val actions = q.result.headOption zip q.delete
    db.run(actions).map(_._1)
  }
}
