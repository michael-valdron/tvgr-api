package controllers

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.Json
import play.api.mvc._
import services.VideoGameDao
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class VideoGameController @Inject()(protected val dbConfigProvider: DatabaseConfigProvider,
                                    controllerComponents: ControllerComponents)
  extends AbstractController(controllerComponents) with HasDatabaseConfigProvider[JdbcProfile] {
  private val dao = new VideoGameDao(db)

  def getAll: Action[AnyContent] = Action.async(dao.getAll.map(entries => Ok(Json.toJson(entries))))

  def getById(entryId: Long): Action[AnyContent] = Action.async {
    dao.get(entryId).map {
      case Some(entry) => Ok(Json.toJson(entry))
      case None => NotFound
    }
  }

  def deleteById(entryId: Long): Action[AnyContent] = Action.async {
    dao.delete(entryId).map {
      case Some(entry) => Ok(Json.toJson(entry))
      case None => NotFound
    }
  }
}
