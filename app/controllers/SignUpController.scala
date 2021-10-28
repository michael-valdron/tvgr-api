package controllers

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import controllers.components.SilhouetteControllerComponents
import handlers.ErrorHandler
import models.User
import play.api.mvc.{Action, AnyContent, Request}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SignUpController @Inject()(components: SilhouetteControllerComponents)(implicit ec: ExecutionContext)
  extends SilhouetteController(components) {
  def signUp: Action[AnyContent] = UnsecuredAction.async { implicit request: Request[AnyContent] =>
    request.body.asJson.flatMap(User.fromJson) match {
      case Some(newUser) if newUser.password.isDefined =>
        userService.retrieve(LoginInfo(CredentialsProvider.ID, newUser.username)).flatMap {
          case Some(_) =>
            Future.successful(
              Conflict(ErrorHandler.createJson(
                request.id.toString,
                "User already exists."))
            )
          case None =>
            val authInfo = passwordHasherRegistry.current.hash(newUser.password.get)
            val user = newUser.copy(password = Some(authInfo.password))
            userService.save(user).map(u => Ok(User.toJson(u.copy(password = None))))
        }
      case _ =>
        Future.successful(
          BadRequest(ErrorHandler.createJson(
            request.id.toString,
            "Invalid JSON for user registration."))
        )
    }
  }
}
