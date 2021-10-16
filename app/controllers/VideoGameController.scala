package controllers

import models.VideoGameEntry
import play.api.libs.json.Json
import play.api.mvc._

import javax.inject.{Inject, Singleton}
import scala.collection.mutable

@Singleton
class VideoGameController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {
  implicit val videoGameJson = Json.format[VideoGameEntry]
  private val videoGameList = new mutable.ListBuffer[VideoGameEntry]

  def getAll: Action[AnyContent] = Action(Ok(Json.toJson(videoGameList)))

  def getById(entryId: Long): Action[AnyContent] = Action {
    val foundEntry = videoGameList.find(_.id == entryId)
    foundEntry match {
      case Some(entry) => Ok(Json.toJson(entry))
      case None => NotFound
    }
  }

  def deleteById(entryId: Long): Action[AnyContent] = Action {
    val foundEntry = videoGameList.find(_.id == entryId)
    foundEntry match {
      case Some(entry) =>
        videoGameList -= entry
        Ok(Json.toJson(foundEntry))
      case None => NotFound
    }
  }
}
