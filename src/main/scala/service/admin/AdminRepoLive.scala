package service.admin
import io.getquill.{PostgresZioJdbcContext, SnakeCase}
import models.Admin
import zio.{Task, ZIO, ZLayer}

import javax.sql.DataSource

case class AdminRepoLive(ds: DataSource) extends AdminRepo {

  private val dsZL = ZLayer.succeed(ds)

  private val ctx = new PostgresZioJdbcContext(SnakeCase)

  import ctx._

  private val adminSchema = quote {
    querySchema[Admin]("public.admin")
  }

  override def getAdmin(username: String): Task[Option[Admin]] = {

    ctx
      .run(adminSchema.filter(_.username == lift(username)))
      .map(_.headOption)
      .provide(dsZL)
  }
}

object AdminRepoLive {
  val layer = ZLayer.fromZIO {
    for {
      ds <- ZIO.service[DataSource]
    } yield AdminRepoLive(ds)
  }
}
