package controllers.components

import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.{Clock, PasswordHasherRegistry}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import models.services.UserService
import utils.auth.JWTEnvironment

trait SilhouetteComponents {
  type EnvType = JWTEnvironment
  type AuthType = EnvType#A
  type IdentityType = EnvType#I

  def userService: UserService
  def authInfoRepository: AuthInfoRepository
  def passwordHasherRegistry: PasswordHasherRegistry
  def clock: Clock
  def credentialsProvider: CredentialsProvider
  def silhouette: Silhouette[JWTEnvironment]
}
