package liquibase

import config.ConfigApp
import zio.macros.accessible
import zio.{RIO, RLayer, Scope, ULayer}

import javax.sql.DataSource

@accessible
trait LiquibaseService {

  def performMigration: RIO[Liquibase, Unit]
}

object LiquibaseService {
  val live: ULayer[LiquibaseService]                                 = LiquibaseServiceLive.layer
  val layer: RLayer[Scope with DataSource with ConfigApp, Liquibase] = LiquibaseServiceLive.liquibaseLayer
}
