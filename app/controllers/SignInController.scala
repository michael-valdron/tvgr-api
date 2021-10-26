package controllers

import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.util.Credentials
import controllers.components.SilhouetteControllerComponents
import models.SignIn
import play.api.i18n.Lang
import play.api.libs.json.JsString
import play.api.mvc.{Action, AnyContent, Request}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SignInController @Inject()(components: SilhouetteControllerComponents)(implicit ec: ExecutionContext)
  extends SilhouetteController(components) {
  def signIn: Action[AnyContent] = UnsecuredAction.async { implicit request: Request[AnyContent] =>
    implicit val lang: Lang = supportedLangs.availables.head
    request.body.asJson.flatMap(SignIn.fromJson) match {
      case Some(login) =>
        val creds = Credentials(login.username, login.password)
        credentialsProvider.authenticate(creds).flatMap { loginInfo =>
          userService.retrieve(loginInfo).flatMap {
            case Some(_) =>
              for {
                auth <- authenticatorService.create(loginInfo)
                token <- authenticatorService.init(auth)
                result <- authenticatorService.embed(token, Ok)
              } yield {
                logger.debug(s"User ${loginInfo.providerKey} has signed in successfully!")
                result
              }
            case None => Future.successful(BadRequest(JsString(messagesApi("could.not.find.user"))))
          }
        }.recover {
          case _: ProviderException => BadRequest(JsString(messagesApi("invalid.credentials")))
        }
      case None => Future.successful(BadRequest(JsString(messagesApi("could.not.find.user"))))
    }
  }
}
