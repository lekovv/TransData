package service.admin

import models.Admin
import zio.{RIO, ZIO}

object AdminService {

  def getAdmin(username: String): RIO[AdminRepo, Option[Admin]] = {
    for {
      service <- ZIO.service[AdminRepo]
      result  <- service.getAdmin(username)
    } yield result
  }
}
