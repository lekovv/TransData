package service.user

import models.UserRequest
import zio.Task
import zio.macros.accessible

import java.util.UUID

@accessible
trait UserRepo {

  def createUser(usersRequest: List[UserRequest]): Task[List[UUID]]
}

object UserRepo {
  val live = UserRepoLive.layer
}
