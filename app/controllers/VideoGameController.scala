package controllers

import models.VideoGameEntry
import play.api.mvc._
import services.VideoGameDao

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class VideoGameController @Inject()(dao: VideoGameDao,
                                    controllerComponents: ControllerComponents)(implicit ec: ExecutionContext)
  extends AbstractController(controllerComponents) {

  def getAll: Action[AnyContent] = Action.async(dao.getAll.map(entries => Ok(VideoGameEntry.toJson(entries))))

  def getById(entryId: Long): Action[AnyContent] = Action.async {
    dao.get(entryId).map {
      case Some(entry) => Ok(VideoGameEntry.toJson(entry))
      case None => NotFound
    }
  }

  def add: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    val input = request.body.asJson
    val parsedEntry = input.flatMap(VideoGameEntry.fromJson)

    parsedEntry match {
      case Some(entry) => dao.add(entry).map {
        case Some(e) => Ok(VideoGameEntry.toJson(e))
        case None => InternalServerError
      }
      case None => Future(BadRequest)
    }
  }

  def deleteById(entryId: Long): Action[AnyContent] = Action.async {
    dao.delete(entryId).map {
      case Some(entry) => Ok(VideoGameEntry.toJson(entry))
      case None => NotFound
    }
  }
}
