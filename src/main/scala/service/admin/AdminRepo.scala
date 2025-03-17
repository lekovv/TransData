package service.admin

import models.Admin
import zio.Task
import zio.macros.accessible

@accessible
trait AdminRepo {

  def getAdmin(username: String): Task[Option[Admin]]
}

object AdminRepo {
  val live = AdminRepoLive.layer
}
