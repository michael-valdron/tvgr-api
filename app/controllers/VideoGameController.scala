package controllers

import models.VideoGameEntry
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc._
import services.VideoGameDao
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class VideoGameController @Inject()(protected val dbConfigProvider: DatabaseConfigProvider,
                                    controllerComponents: ControllerComponents)
  extends AbstractController(controllerComponents) with HasDatabaseConfigProvider[JdbcProfile] {
  private val dao = new VideoGameDao(db)

  def getAll: Action[AnyContent] = Action.async(dao.getAll.map(entries => Ok(VideoGameEntry.toJson(entries))))

  def getById(entryId: Long): Action[AnyContent] = Action.async {
    dao.get(entryId).map {
      case Some(entry) => Ok(VideoGameEntry.toJson(entry))
      case None => NotFound
    }
  }

  def add: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    val input = request.body.asJson
    val parsedInput = input.flatMap(VideoGameEntry.fromJson)

    if (parsedInput.nonEmpty) {
      parsedInput.get match {
        case Left(entry) => dao.add(entry).map {
          case Some(e) => Ok(VideoGameEntry.toJson(e))
          case None => InternalServerError
        }
        case Right(_) => Future(UnprocessableEntity)
      }
    } else Future(BadRequest)
  }

  def deleteById(entryId: Long): Action[AnyContent] = Action.async {
    dao.delete(entryId).map {
      case Some(entry) => Ok(VideoGameEntry.toJson(entry))
      case None => NotFound
    }
  }
}
