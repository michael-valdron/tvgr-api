package handlers

import play.api.http.JsonHttpErrorHandler
import play.api.libs.json.Json
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
        NotFound(Json.toJson(Map(
          "error" -> Map(
            "requestId" -> request.id.toString,
            "message" -> "Not Found"
          ))))
      )
    else
      super.onClientError(request, statusCode, message)
  }

  override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    Future.successful(
      InternalServerError(Json.toJson(Map(
        "error" -> Map(
          "requestId" -> request.id.toString,
          "message" -> "Internal Server Error"
        ))))
    )
  }
}
