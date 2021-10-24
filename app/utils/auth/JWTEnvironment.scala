package utils.auth

import com.mohiva.play.silhouette.api.Env
import models.User

trait JWTEnvironment extends Env {
  type I = User
  type A = JWTEnvironment
}
