package service.user

import models.UserRequest
import zio.{RIO, ZIO}

import java.util.UUID

object UserService {

  def createUser(userRequest: List[UserRequest]): RIO[UserRepo, List[UUID]] =
    for {
      service <- ZIO.service[UserRepo]
      result  <- service.createUser(userRequest)
    } yield result
}
