import auth.AuthLive
import config.ConfigApp
import liquibase.LiquibaseService
import scheduler.SchedulerLive
import service.admin.AdminRepo
import service.spark.{Spark, SparkLive}
import service.transaction.TransactionRepo
import service.user.UserRepo
import zio._
import zio.http._
import zio.http.netty.NettyConfig
import zio.http.netty.NettyConfig.LeakDetectionLevel
object Layers {

  private val serverConf = ZLayer.fromZIO {
    ZIO.config[ConfigApp].map { config =>
      Server.Config.default.port(config.interface.port)
    }
  }

  private val nettyConf = ZLayer.succeed(
    NettyConfig.default
      .leakDetection(LeakDetectionLevel.DISABLED)
  )

  private val runtime = Scope.default

  private lazy val server = (serverConf ++ nettyConf) >>> Server.customized

  private val client = ZClient.default

  private val base = ConfigApp.live >+> DBContext.live

  val all =
    runtime >+>
      base >+>
      LiquibaseService.live >+>
      LiquibaseService.layer >+>
      client >+>
      server >+>
      AdminRepo.live >+>
      AuthLive.layer >+>
      SchedulerLive.layer >+>
      UserRepo.live >+>
      TransactionRepo.live >+>
      Spark.live >+>
      SparkLive.layer
}
