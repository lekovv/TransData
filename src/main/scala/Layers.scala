import config.ConfigApp
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
      client >+>
      server >+>
      UserRepo.live >+>
      TransactionRepo.live
}
