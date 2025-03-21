package service.admin
import exception.AppError
import exception.AppError.InternalException
import io.getquill.{PostgresZioJdbcContext, SnakeCase}
import models.Admin
import zio.{IO, URLayer, ZIO, ZLayer}

import javax.sql.DataSource

case class AdminRepoLive(ds: DataSource) extends AdminRepo {

  private val dsZL = ZLayer.succeed(ds)

  private val ctx = new PostgresZioJdbcContext(SnakeCase)

  import ctx._

  private val adminSchema = quote {
    querySchema[Admin]("public.admin")
  }

  override def getAdmin(username: String): IO[AppError, Option[Admin]] = {

    ctx
      .run(adminSchema.filter(_.username == lift(username)))
      .mapBoth(
        err => InternalException(err.getMessage),
        _.headOption
      )
      .provide(dsZL)
  }
}

object AdminRepoLive {
  val layer: URLayer[DataSource, AdminRepoLive] = ZLayer.fromZIO {
    for {
      ds <- ZIO.service[DataSource]
    } yield AdminRepoLive(ds)
  }
}
