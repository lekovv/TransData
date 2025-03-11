package service.user

import models.UserRequest
import zio.Task
import zio.macros.accessible

import java.util.UUID

@accessible
trait UserRepo {

  def createUser(userRequest: UserRequest): Task[UUID]
}

object UserRepo {
  val live = UserRepoLive.layer
}
