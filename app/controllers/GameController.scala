package controllers

import models.dao.GameDao
import models.Game
import play.api.libs.json.Json
import play.api.mvc._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GameController @Inject()(dao: GameDao,
                               controllerComponents: ControllerComponents)(implicit ec: ExecutionContext)
  extends AbstractController(controllerComponents) {

  def getAll: Action[AnyContent] = Action.async(dao.getAll.map(entries => Ok(Game.toJson(entries))))

  def getById(entryId: Long): Action[AnyContent] = Action.async {
    dao.get(entryId).map {
      case Some(entry) => Ok(Game.toJson(entry))
      case None => NotFound
    }
  }

  def add: Action[AnyContent] = Action.async { request =>
    val input = request.body.asJson
    val parsedEntry = input.flatMap(Game.fromJson)

    parsedEntry match {
      case Some(entry) => dao.add(entry).map {
        case Some(e) => Ok(Game.toJson(e))
        case None => InternalServerError
      }
      case None => Future(BadRequest)
    }
  }

  def editById(entryId: Long): Action[AnyContent] = Action.async { request =>
    val input =  request.body.asJson
    val parsedResult = input.map(Json.fromJson[Map[String, String]])
    val data: Map[String, Any] = parsedResult match {
      case Some(result) => if (result.isSuccess) result.get else Map.empty[String, String]
      case None => Map.empty[String, String]
    }
    if (data.isEmpty)
      Future(BadRequest(Json.toJson(Map.empty[String, String])))
    else {
      val entry = Game.fromMap(data + ("id" -> entryId))
      dao.edit(entry).map {
        case Some(result) => Ok(Game.toJson(result))
        case None => NotFound(Json.toJson(Map.empty[String, String]))
      }
    }
  }

  def deleteById(entryId: Long): Action[AnyContent] = Action.async {
    dao.delete(entryId).map {
      case Some(entry) => Ok(Game.toJson(entry))
      case None => NotFound
    }
  }
}