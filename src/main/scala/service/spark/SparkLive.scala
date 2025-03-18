package service.spark
import config.ConfigApp
import exception.SparkError
import exception.SparkError.{SparkCalculateException, SparkReadException, SparkSaveException}
import models.Metric
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._
import zio.{IO, URLayer, ZIO, ZLayer}

case class SparkLive(
    spark: Spark,
    url: String,
    user: String,
    password: String
) {

  private def readTransactions(): IO[SparkError, DataFrame] = ZIO
    .attempt {

      spark.session.read
        .format("jdbc")
        .option("url", url)
        .option("driver", "org.postgresql.Driver")
        .option("dbtable", "public.transaction")
        .option("user", user)
        .option("password", password)
        .load()
    }
    .mapError(err => SparkReadException(s"failed to read transactions ${err.getMessage}"))

  private def readUsers(): IO[SparkError, DataFrame] = ZIO
    .attempt {

      spark.session.read
        .format("jdbc")
        .option("url", url)
        .option("driver", "org.postgresql.Driver")
        .option("dbtable", "public.user")
        .option("user", user)
        .option("password", password)
        .load()
    }
    .mapError(err => SparkReadException(s"failed to read users ${err.getMessage}"))

  private def calculateMetrics(dfTrans: DataFrame, dfUsers: DataFrame): IO[SparkError, List[Metric]] = ZIO
    .attempt {

      val transDF = dfTrans.alias("trans")
      val usersDF = dfUsers.alias("users")

      val amount = dfTrans.agg(
        sum("amount").alias("total_amount"),
        avg("amount").alias("avg_amount")
      )

      val topUsers = transDF
        .join(
          usersDF,
          transDF("trans.user_id") === usersDF("users.id")
        )
        .groupBy("users.id")
        .agg(
          concat(any_value(col("users.first_name")), lit(" "), any_value(col("users.last_name"))).alias("fio"),
          any_value(col("users.email")).alias("email"),
          sum("trans.amount").alias("total_user_amount")
        )
        .orderBy(col("total_user_amount").desc)

      val countryStats = transDF
        .join(
          usersDF,
          transDF("trans.user_id") === usersDF("users.id")
        )
        .groupBy("users.country")
        .agg(
          count("*").alias("transaction_count"),
          sum("trans.amount").alias("total_amount")
        )

      List(
        Metric("amount", amount),
        Metric("top_users", topUsers.drop("id")),
        Metric("country_stats", countryStats)
      )
    }
    .mapError(err => SparkCalculateException(s"failed to calculate metric ${err.getMessage}"))

  private def saveMetrics(metrics: List[Metric]): IO[SparkError, List[Unit]] = ZIO
    .attempt {

      metrics.map { metric =>
        metric.df
          .withColumn("created", current_date())
          .write
          .format("jdbc")
          .option("url", url)
          .option("driver", "org.postgresql.Driver")
          .option("dbtable", s"public.${metric.name}")
          .option("user", user)
          .option("password", password)
          .mode("append")
          .save()
      }
    }
    .mapError(err => SparkSaveException(s"failed to calculate metric ${err.getMessage}"))

  def analyzeData(): IO[SparkError, Unit] = {
    for {
      transactions <- readTransactions()
      users        <- readUsers()
      metrics      <- calculateMetrics(transactions, users)
      _            <- saveMetrics(metrics)
      _            <- ZIO.logInfo("Metrics saved successfully")
    } yield ()
  }

  def sendData(): IO[SparkError, List[Metric]] = {
    for {
      transactions <- readTransactions()
      users        <- readUsers()
      metrics      <- calculateMetrics(transactions, users)
    } yield metrics
  }
}

object SparkLive {

  val layer: URLayer[ConfigApp with Spark, SparkLive] = ZLayer.fromZIO {
    for {
      spark  <- ZIO.service[Spark]
      config <- ZIO.service[ConfigApp]
      url      = config.spark.url
      user     = config.spark.user
      password = config.spark.password
    } yield SparkLive(spark, url, user, password)
  }
}
