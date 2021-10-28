package models.dao

import models.Game
import models.tables.Games
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

/**
 * Data Access Object for games relation.
 *
 * @param dbConfigProvider Database configuration provider
 * @param ec Execution context for managing concurrency
 */
class GameDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._

  /**
   * Fetches `Game` entity by id. Returns None if non-existent.
   *
   * @param entryId Unique identifier for a `Game` record
   * @return `Game` entity from database
   */
  def get(entryId: Long): Future[Option[Game]] =
    db.run(Games.filterById(entryId).result.headOption)

  /**
   * Fetches all `Game` entities.
   *
   * @return Sequence of `Game` entities from database
   */
  def getAll: Future[Seq[Game]] = db.run(Games.result)

  /**
   * Add `Game` entity to games relation. Returns None if not successfully added.
   *
   * @param entry `Game` entity to add
   * @return Recently added `Game` entity
   */
  def add(entry: Game): Future[Option[Game]] =
    db.run((Games += entry) andThen Games.filterById(entry.id).result.headOption)

  /**
   * Update game record with modified `Game` entity. Returns None if not successfully edited.
   *
   * @param entry Modified `Game` entity
   * @return Updated `Game` entity
   */
  def edit(entry: Game): Future[Option[Game]] = {
    val q = Games.filterById(entry.id)
    db.run(q.update(entry) andThen q.result.headOption)
  }

  /**
   * Deletes `Game` entity by id and returns it. Returns None if non-existent.
   *
   * @param entryId Unique identifier for a `Game` record
   * @return `Game` entity deleted from database
   */
  def delete(entryId: Long): Future[Option[Game]] = {
    val q = Games.filterById(entryId)
    db.run(q.result.headOption zip q.delete).map(_._1)
  }
}
