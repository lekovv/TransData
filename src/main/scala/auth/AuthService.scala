package auth

import exception.AuthError
import models.Login
import service.admin.AdminRepo
import zio.ZIO

object AuthService {

  def authentication(login: Login): ZIO[AdminRepo with AuthLive, AuthError, String] = {
    for {
      service <- ZIO.service[AuthLive]
      result  <- service.authentication(login)
    } yield result
  }
}
