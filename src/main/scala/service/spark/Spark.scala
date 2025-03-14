package service.spark

import org.apache.spark.sql.SparkSession
import zio.{ZIO, ZLayer}

case class Spark(session: SparkSession)

object Spark {
  val live: ZLayer[Any, Throwable, Spark] = ZLayer.fromZIO {
    ZIO
      .attempt {
        SparkSession
          .builder()
          .appName("TransData")
          .master("local[*]")
          .getOrCreate()
      }
      .map(Spark(_))
  }
}
