package service.user

import exception.AppError.InternalDatabaseException
import models.UserRequest
import zio.macros.accessible
import zio.{IO, URLayer}

import java.util.UUID
import javax.sql.DataSource

@accessible
trait UserRepo {

  def createUser(usersRequest: List[UserRequest]): IO[InternalDatabaseException, List[UUID]]
}

object UserRepo {
  val live: URLayer[DataSource, UserRepoLive] = UserRepoLive.layer
}
