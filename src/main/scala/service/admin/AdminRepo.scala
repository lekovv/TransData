package service.admin

import models.Admin
import zio.IO
import zio.macros.accessible

import java.sql.SQLException

@accessible
trait AdminRepo {

  def getAdmin(username: String): IO[SQLException, Option[Admin]]
}

object AdminRepo {
  val live = AdminRepoLive.layer
}
