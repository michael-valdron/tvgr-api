package services

import models.{VideoGameEntry, VideoGames}
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

class VideoGameDao(db: Database)(implicit ec: ExecutionContext) {
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

  def delete(entryId: Long): Future[Option[VideoGameEntry]] = {
    val q = games.filter(_.id === entryId)
    val actions = q.result.headOption zip q.delete
    db.run(actions).map(_._1)
  }
}
