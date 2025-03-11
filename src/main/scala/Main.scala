import endpoints.Health
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

  private val allRoutes = Health.routes

  private def startServer = Server.serve(allRoutes)

  private val program =
    for {
      _ <- ZIO.logInfo("Server is running")
      _ <- startServer.exitCode
    } yield ()

  override def run: UIO[ExitCode] = {
    program
      .provide(Layers.all)
      .foldZIO(
        err => printLine(s"Execution failed with: $err").exitCode,
        _ => ZIO.succeed(ExitCode.success)
      )
  }
}
