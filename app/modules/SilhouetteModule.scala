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
import utils.auth.JWTEnvironment

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.{Duration, FiniteDuration}

/**
 * Custom play / scala module for defining providers of authentication
 * processes using the silhouette library.
 *
 */
class SilhouetteModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[Silhouette[JWTEnvironment]].to[SilhouetteProvider[JWTEnvironment]]
    bind[IDGenerator].toInstance(new SecureRandomIDGenerator())
    bind[EventBus].toInstance(EventBus())
    bind[Clock].toInstance(Clock())
  }

  /**
   * Provides JWT environment.
   *
   * @param userService The user service to be used in the JWT environment
   * @param authenticatorService The authentication service to be used in the JWT environment
   * @param eventBus The silhouette event bus for managing authentication events
   * @return The JWT environment entity
   */
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

  /**
   * Provides authentication encoding crypter created from the secret key.
   *
   * @param configuration Play application configuration where the secret key is defined
   * @return An authentication crypter
   */
  @Provides
  def provideAuthenticatorCrypter(configuration: Configuration): Crypter =
    new JcaCrypter(JcaCrypterSettings(configuration.underlying.getString("play.http.secret.key")))

  /**
   * Provides the JWT authenticator service.
   *
   * @param crypter The crypter to use with the encoder
   * @param idGenerator A generator which creates an ID for the authenticator
   * @param configuration Silhouette configuration
   * @param clock A time entity for calculating the expiry
   * @return The JWT authenticator service
   */
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

  /**
   * Provides an instance of `PasswordInfoDao`.
   *
   * @param userDao An instance of `UserDao` to use with `PasswordInfoDao`
   * @return An instance of `PasswordInfoDao` coupled with the passed instance of `UserDao`
   */
  @Provides
  def providePasswordDao(userDao: UserDao): DelegableAuthInfoDAO[PasswordInfo] = new PasswordInfoDao(userDao)

  /**
   * Provides an instance of authentication information repository.
   *
   * @param passwordInfoDao An instance of `PasswordInfoDao` to use with `DelegableAuthInfoRepository`
   * @return An instance of `DelegableAuthInfoRepository` coupled with the passed instance of `PasswordInfoDao`
   */
  @Provides
  def provideAuthInfoRepository(passwordInfoDao: DelegableAuthInfoDAO[PasswordInfo]): AuthInfoRepository =
    new DelegableAuthInfoRepository(passwordInfoDao)

  /**
   * Provides a password hashing registry.
   *
   * @return A password hashing registry using BCryptSha256
   */
  @Provides
  def providePasswordHasherRegistry(): PasswordHasherRegistry =
    PasswordHasherRegistry(new BCryptSha256PasswordHasher(), Seq(new BCryptPasswordHasher()))

  /**
   * Provides a credentials provider.
   *
   * @param authInfoRepository Authentication information repository to use with credentials provider
   * @param passwordHasherRegistry Password hashing registry to use with credentials provider
   * @return Newly created credentials provider
   */
  @Provides
  def provideCredentialsProvider(authInfoRepository: AuthInfoRepository,
                                 passwordHasherRegistry: PasswordHasherRegistry): CredentialsProvider =
    new CredentialsProvider(authInfoRepository, passwordHasherRegistry)

  /**
   * Provides the default custom controller components `DefaultSilhouetteControllerComponents`
   * needed for authentication.
   *
   * @param components The default custom controller components with authentication / silhouette members
   * @return The controller components as the trait `SilhouetteControllerComponents`
   */
  @Provides
  def provideSilhouetteComponents(components: DefaultSilhouetteControllerComponents): SilhouetteControllerComponents =
    components
}
