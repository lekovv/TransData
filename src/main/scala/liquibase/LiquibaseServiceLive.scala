package liquibase

import config.{ConfigApp, Liquibase => LiquibaseConfig}
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.{ClassLoaderResourceAccessor, CompositeResourceAccessor, FileSystemResourceAccessor}
import zio.{RIO, RLayer, Scope, ULayer, ZIO, ZLayer}

import javax.sql.DataSource

final case class LiquibaseServiceLive() extends LiquibaseService {

  override def performMigration: RIO[Liquibase, Unit] = ZIO.serviceWith[Liquibase](_.update("trans_data"))
}

object LiquibaseServiceLive {

  def layer: ULayer[LiquibaseService] = ZLayer.succeed(LiquibaseServiceLive())

  def liquibaseLayer: RLayer[Scope with DataSource with ConfigApp, Liquibase] =
    ZLayer.fromZIO(
      for {
        service <- ZIO.service[ConfigApp]
        config = service.liquibase
        liquibase <- make(config)
      } yield liquibase
    )

  private def make(config: LiquibaseConfig): RIO[Scope with DataSource, Liquibase] =
    for {
      dataSource          <- ZIO.service[DataSource]
      fileAccessor        <- ZIO.from(new FileSystemResourceAccessor())
      classLoader         <- ZIO.from(classOf[LiquibaseService].getClassLoader)
      classLoaderAccessor <- ZIO.from(new ClassLoaderResourceAccessor(classLoader))
      fileOpener          <- ZIO.from(new CompositeResourceAccessor(fileAccessor, classLoaderAccessor))
      jdbcConnection      <- ZIO.acquireRelease(ZIO.from(new JdbcConnection(dataSource.getConnection)))(c => ZIO.succeed(c.close()))
      liquibase           <- ZIO.from(new Liquibase(config.changeLog, fileOpener, jdbcConnection))
    } yield liquibase
}
