package handlers

import play.api.http.JsonHttpErrorHandler
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.OFormat.oFormatFromReadsAndOWrites
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Results.{InternalServerError, NotFound}
import play.api.mvc.{RequestHeader, Result}
import play.api.{Environment, OptionalSourceMapper}

import javax.inject.Inject
import scala.concurrent.Future

class ErrorHandler @Inject()(environment: Environment, sourceMapper: OptionalSourceMapper)
  extends JsonHttpErrorHandler(environment, sourceMapper) {
  override def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    if (statusCode == 404)
      Future.successful(
        NotFound(ErrorHandler.createJson(request.id.toString, if (message == "") "Not Found" else message))
      )
    else
      super.onClientError(request, statusCode, message)
  }

  override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    Future.successful {
      exception.printStackTrace()
      InternalServerError(ErrorHandler.createJson(request.id.toString, "Internal Server Error"))
    }
  }
}

object ErrorHandler {
  def createJson(requestId: String, message: String): JsValue =
    Json.toJson(Map(
      "error" -> Map(
        "requestId" -> requestId,
        "message" -> message)))
}