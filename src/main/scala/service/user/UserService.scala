package service.user

import exception.Exceptions.InternalDatabaseException
import models.UserRequest
import zio.ZIO

import java.util.UUID

object UserService {

  def createUser(userRequest: List[UserRequest]): ZIO[UserRepo, InternalDatabaseException, List[UUID]] =
    for {
      service <- ZIO.service[UserRepo]
      result  <- service.createUser(userRequest)
    } yield result
}
