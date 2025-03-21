package auth

import exception.AppError
import models.Login
import service.admin.AdminRepo
import zio.ZIO

object AuthService {

  def authentication(login: Login): ZIO[AdminRepo with AuthLive, AppError, String] = {
    for {
      service <- ZIO.service[AuthLive]
      result  <- service.authentication(login)
    } yield result
  }
}
