package service.admin

import exception.AuthError
import models.Admin
import zio.macros.accessible
import zio.{IO, URLayer}

import javax.sql.DataSource

@accessible
trait AdminRepo {

  def getAdmin(username: String): IO[AuthError, Option[Admin]]
}

object AdminRepo {
  val live: URLayer[DataSource, AdminRepoLive] = AdminRepoLive.layer
}
