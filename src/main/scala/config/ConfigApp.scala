package config

import zio._
import zio.config._
import zio.config.magnolia.deriveConfig

case class Interface(
    host: String,
    port: Int
)

case class Spark(
    url: String,
    user: String,
    password: String
)

case class ConfigApp(
    interface: Interface,
    spark: Spark
)

object ConfigApp {

  implicit val configDescriptor: Config[ConfigApp] = (
    deriveConfig[Interface].nested("interface") zip
      deriveConfig[Spark].nested("sparkConfig")
  )
    .to[ConfigApp]
    .mapKey(toKebabCase)

  val live: ZLayer[Any, Config.Error, ConfigApp] = ZLayer.fromZIO {
    ZIO.config[ConfigApp](configDescriptor)
  }
}
