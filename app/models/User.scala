package models

import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.api.{Identity, LoginInfo}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.mohiva.play.silhouette.password.BCryptSha256PasswordHasher

case class User(username: String, password: Option[String] = None) extends Identity {
  def loginInfo: LoginInfo = LoginInfo(CredentialsProvider.ID, username)
  def passwordInfo: PasswordInfo = PasswordInfo(BCryptSha256PasswordHasher.ID, password.get)
}
