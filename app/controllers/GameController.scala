package controllers

import handlers.ErrorHandler
import models.Game
import models.dao.GameDao
import play.api.Logging
import play.api.mvc._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GameController @Inject()(dao: GameDao,
                               controllerComponents: ControllerComponents)(implicit ec: ExecutionContext)
  extends AbstractController(controllerComponents) with Logging {

  def getAll: Action[AnyContent] = Action.async {
    dao.getAll.map { entries =>
      logger.info(s"${entries.length} games where successfully fetched from the database.")
      Ok(Game.toJson(entries))
    }
  }

  def getById(entryId: Long): Action[AnyContent] = Action.async { request =>
    dao.get(entryId).map {
      case Some(entry) =>
        logger.info(s"An game entry with id = ${entry.id} was successfully fetched from the database.")
        Ok(Game.toJson(entry))
      case None =>
        NotFound(ErrorHandler.createJson(
          request.id.toString,
          s"Cannot find game $entryId."))
    }
  }

  def add: Action[AnyContent] = Action.async { request =>
    val input = request.body.asJson
    val parsedEntry = input.flatMap(Game.fromJson)

    parsedEntry match {
      case Some(entry) => dao.get(entry.id).flatMap {
        case Some(_) =>
          Future.successful(
            Conflict(ErrorHandler.createJson(
              request.id.toString,
              s"Game with id = ${entry.id} already exists."))
          )
        case None => dao.add(entry).map {
          case Some(e) =>
            logger.info(s"An game entry with id = ${entry.id} was successfully added to the database.")
            Ok(Game.toJson(e))
          case None =>
            logger.error("Something went wrong with a request to add a game.")
            InternalServerError(ErrorHandler.createJson(request.id.toString, "Internal Server Error"))
        }
      }
      case None =>
        Future.successful(
          BadRequest(ErrorHandler.createJson(
            request.id.toString,
            "Input body is an invalid JSON form for a game."))
        )
    }
  }

  def edit: Action[AnyContent] = Action.async { request =>
    val input =  request.body.asJson
    val parsedEntry = input.flatMap(Game.fromJson)

    parsedEntry match {
      case Some(entry) => dao.get(entry.id).flatMap {
        case Some(_) => dao.edit(entry).map {
          case Some(result) =>
            logger.info(s"Game entry with id = ${entry.id} was successfully edited.")
            Ok(Game.toJson(result))
          case None =>
            logger.error(s"Something went wrong with a request to edit the game with id = ${entry.id}.")
            InternalServerError(ErrorHandler.createJson(request.id.toString, "Internal Server Error"))
        }
        case None =>
          Future.successful(
            NotFound(ErrorHandler.createJson(
              request.id.toString,
              s"Game record with id = ${entry.id} was not found."))
          )
      }
      case None =>
        Future.successful(
          BadRequest(ErrorHandler.createJson(
            request.id.toString,
            "Input body is an invalid JSON form for a game."))
        )
    }
  }

  def deleteById(entryId: Long): Action[AnyContent] = Action.async { request =>
    dao.get(entryId).flatMap {
      case Some(_) => dao.delete(entryId).map {
        case Some(entry) => Ok(Game.toJson(entry))
        case None =>
          logger.error(s"Something went wrong with a request to remove the game with id = $entryId.")
          InternalServerError(ErrorHandler.createJson(request.id.toString, "Internal Server Error"))
      }
      case None =>
        Future.successful(
          NotFound(ErrorHandler.createJson(
            request.id.toString,
            s"Game record with id = $entryId was not found."))
        )
    }
  }
}
