package modules

import com.google.inject.{AbstractModule, Provides}
import com.mohiva.play.silhouette.api.crypto.{Crypter, CrypterAuthenticatorEncoder}
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AuthenticatorService
import com.mohiva.play.silhouette.api.util._
import com.mohiva.play.silhouette.api.{Environment, EventBus, Silhouette, SilhouetteProvider}
import com.mohiva.play.silhouette.crypto.{JcaCrypter, JcaCrypterSettings}
import com.mohiva.play.silhouette.impl.authenticators.{JWTAuthenticator, JWTAuthenticatorService, JWTAuthenticatorSettings}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.mohiva.play.silhouette.impl.util.SecureRandomIDGenerator
import com.mohiva.play.silhouette.password.{BCryptPasswordHasher, BCryptSha256PasswordHasher}
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import com.mohiva.play.silhouette.persistence.repositories.DelegableAuthInfoRepository
import controllers.components.{DefaultSilhouetteControllerComponents, SilhouetteControllerComponents}
import models.dao.{PasswordInfoDao, UserDao}
import models.services.UserService
import net.codingwell.scalaguice.ScalaModule
import play.api.Configuration
import play.api.libs.ws.WSClient
import utils.auth.JWTEnvironment

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.{Duration, FiniteDuration}

class SilhouetteModule()(implicit ec: ExecutionContext) extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[Silhouette[JWTEnvironment]].to[SilhouetteProvider[JWTEnvironment]]
    bind[IDGenerator].toInstance(new SecureRandomIDGenerator())
    bind[EventBus].toInstance(EventBus())
    bind[Clock].toInstance(Clock())
  }

  @Provides
  def provideHttpLayer(client: WSClient): HTTPLayer = new PlayHTTPLayer(client)

  @Provides
  def provideEnvironment(userService: UserService,
                         authenticatorService: AuthenticatorService[JWTAuthenticator],
                         eventBus: EventBus): Environment[JWTEnvironment] =
    Environment[JWTEnvironment](
      userService,
      authenticatorService,
      Seq.empty,
      eventBus
    )

  @Provides
  def provideAuthenticatorCrypter(configuration: Configuration): Crypter =
    new JcaCrypter(JcaCrypterSettings(configuration.underlying.getString("play.http.secret.key")))

  @Provides
  def provideAuthenticatorService(crypter: Crypter,
                                  idGenerator: IDGenerator,
                                  configuration: Configuration,
                                  clock: Clock): AuthenticatorService[JWTAuthenticator] = {
    val encoder = new CrypterAuthenticatorEncoder(crypter)
    new JWTAuthenticatorService(JWTAuthenticatorSettings(
      fieldName = configuration.underlying.getString("silhouette.authenticator.headerName"),
      issuerClaim = configuration.underlying.getString("silhouette.authenticator.issuerClaim"),
      authenticatorExpiry = Duration(
        configuration.underlying.getString("silhouette.authenticator.authenticatorExpiry")
      ).asInstanceOf[FiniteDuration],
      sharedSecret = configuration.underlying.getString("silhouette.authenticator.sharedSecret")
    ), None, encoder, idGenerator, clock)
  }

  @Provides
  def providePasswordDao(userDao: UserDao): DelegableAuthInfoDAO[PasswordInfo] = new PasswordInfoDao(userDao)

  @Provides
  def provideAuthInfoRepository(passwordInfoDao: DelegableAuthInfoDAO[PasswordInfo]): AuthInfoRepository =
    new DelegableAuthInfoRepository(passwordInfoDao)

  @Provides
  def providePasswordHasherRegistry(): PasswordHasherRegistry =
    PasswordHasherRegistry(new BCryptSha256PasswordHasher(), Seq(new BCryptPasswordHasher()))

  @Provides
  def provideCredentialsProvider(authInfoRepository: AuthInfoRepository,
                                 passwordHasherRegistry: PasswordHasherRegistry): CredentialsProvider =
    new CredentialsProvider(authInfoRepository, passwordHasherRegistry)

  @Provides
  def provideSilhouetteComponents(components: DefaultSilhouetteControllerComponents): SilhouetteControllerComponents =
    components
}
