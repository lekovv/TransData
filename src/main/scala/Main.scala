import endpoints.Endpoints
import liquibase.LiquibaseService
import scheduler.SchedulerLive
import service.spark.SparkLive
import zio.Console.printLine
import zio.Runtime.setConfigProvider
import zio._
import zio.config.typesafe.TypesafeConfigProvider
import zio.http._
import zio.logging.backend.SLF4J

object Main extends ZIOAppDefault {
  override val bootstrap: ZLayer[ZIOAppArgs, Any, Any] =
    Runtime.removeDefaultLoggers >>> SLF4J.slf4j >>>
      setConfigProvider(
        TypesafeConfigProvider
          .fromResourcePath()
      )

  private val allRoutes = Endpoints.routes ++ Endpoints.loginRoute

  private def startServer = Server.serve(allRoutes)

  private val program =
    for {
      _       <- ZIO.serviceWithZIO[LiquibaseService](_.performMigration)
      _       <- ZIO.logInfo("Server is running")
      http    <- startServer.exitCode.fork
      spark   <- ZIO.service[SparkLive]
      metrics <- ZIO.serviceWithZIO[SchedulerLive](_.repeat24hours(() => spark.analyzeData())).fork
      code    <- http.join *> metrics.join
    } yield code

  override def run: UIO[ExitCode] = {
    program
      .provide(Layers.all)
      .foldZIO(
        err => printLine(s"Execution failed with: $err").exitCode,
        _ => ZIO.succeed(ExitCode.success)
      )
  }
}
