package service.admin

import exception.AppError
import models.Admin
import zio.macros.accessible
import zio.{IO, URLayer}

import javax.sql.DataSource

@accessible
trait AdminRepo {

  def getAdmin(username: String): IO[AppError, Option[Admin]]
}

object AdminRepo {
  val live: URLayer[DataSource, AdminRepoLive] = AdminRepoLive.layer
}
