package controllers.components

import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.{Clock, PasswordHasherRegistry}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import models.services.UserService
import play.api.http.FileMimeTypes
import play.api.i18n.{Langs, MessagesApi}
import play.api.mvc.{DefaultActionBuilder, MessagesActionBuilder, PlayBodyParsers}
import utils.auth.JWTEnvironment

import javax.inject.Inject
import scala.concurrent.ExecutionContext

final  case class DefaultSilhouetteControllerComponents @Inject()
(silhouette: Silhouette[JWTEnvironment],
 userService: UserService,
 authInfoRepository: AuthInfoRepository,
 passwordHasherRegistry: PasswordHasherRegistry,
 clock: Clock,
 credentialsProvider: CredentialsProvider,
 messagesActionBuilder: MessagesActionBuilder,
 actionBuilder: DefaultActionBuilder,
 parsers: PlayBodyParsers,
 messagesApi: MessagesApi,
 langs: Langs,
 fileMimeTypes: FileMimeTypes,
 executionContext: ExecutionContext) extends SilhouetteControllerComponents
