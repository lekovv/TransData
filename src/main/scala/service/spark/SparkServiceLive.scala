package service.spark
import config.ConfigApp
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._
import zio.{Task, ZIO, ZLayer}

//TODO: добавить валюты и дату создания
case class SparkServiceLive(
    spark: Spark,
    url: String,
    user: String,
    password: String
) {

  private def readTransactions(): Task[DataFrame] = ZIO.attempt {

    spark.session.read
      .format("jdbc")
      .option("url", url)
      .option("driver", "org.postgresql.Driver")
      .option("dbtable", "public.transaction")
      .option("user", user)
      .option("password", password)
      .load()
  }

  private def readUsers(): Task[DataFrame] = ZIO.attempt {

    spark.session.read
      .format("jdbc")
      .option("url", url)
      .option("driver", "org.postgresql.Driver")
      .option("dbtable", "public.user")
      .option("user", user)
      .option("password", password)
      .load()
  }

  private def calculateMetrics(dfTrans: DataFrame, dfUsers: DataFrame): Task[(DataFrame, DataFrame, DataFrame, DataFrame)] = ZIO.attempt {

    val transDF = dfTrans.alias("trans")
    val usersDF = dfUsers.alias("users")

    val totalAmount = dfTrans.agg(sum("amount").alias("total_amount"))

    val avgAmount = dfTrans.agg(avg("amount").alias("avg_amount"))

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

    (totalAmount, avgAmount, topUsers, countryStats)
  }

  private def saveMetrics(
      totalAmount: DataFrame,
      avgAmount: DataFrame,
      topUsers: DataFrame,
      countryStats: DataFrame
  ): Task[Unit] =
    ZIO.attempt {

      totalAmount.write
        .format("jdbc")
        .option("url", url)
        .option("driver", "org.postgresql.Driver")
        .option("dbtable", "public.total_amount")
        .option("user", user)
        .option("password", password)
        .mode("append")
        .save()

      avgAmount.write
        .format("jdbc")
        .option("url", url)
        .option("driver", "org.postgresql.Driver")
        .option("dbtable", "public.avg_amount")
        .option("user", user)
        .option("password", password)
        .mode("append")
        .save()

      topUsers
        .drop("id")
        .write
        .format("jdbc")
        .option("url", url)
        .option("driver", "org.postgresql.Driver")
        .option("dbtable", "public.top_users")
        .option("user", user)
        .option("password", password)
        .mode("append")
        .save()

      countryStats.write
        .format("jdbc")
        .option("url", url)
        .option("driver", "org.postgresql.Driver")
        .option("dbtable", "public.country_stats")
        .option("user", user)
        .option("password", password)
        .mode("append")
        .save()
    }

  def analyzeData(): Task[Unit] = {

    for {
      transactions <- readTransactions()
      users        <- readUsers()
      metrics      <- calculateMetrics(transactions, users)
      _            <- saveMetrics(metrics._1, metrics._2, metrics._3, metrics._4)
      _            <- ZIO.logInfo("Metrics saved successfully")
    } yield ()
  }
}

object SparkServiceLive {

  val layer: ZLayer[ConfigApp with Spark, Nothing, SparkServiceLive] = ZLayer.fromZIO {
    for {
      spark  <- ZIO.service[Spark]
      config <- ZIO.service[ConfigApp]
      url      = config.spark.url
      user     = config.spark.user
      password = config.spark.password
    } yield SparkServiceLive(spark, url, user, password)
  }
}
