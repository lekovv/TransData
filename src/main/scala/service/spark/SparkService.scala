package service.spark

import exception.SparkError
import exception.SparkError.MetricNotFoundException
import models.{AmountModel, CountryStatsModel, TopUsersModel}
import zio.ZIO

object SparkService {

  def getAmountMetric: ZIO[SparkLive, SparkError, AmountModel] = {
    for {
      spark   <- ZIO.service[SparkLive]
      metrics <- spark.sendData()
      amount = metrics.find(_.name == "amount")
      result <- amount match {
        case Some(value) =>
          val totalAmount = value.df.select("total_amount").first().getDecimal(0)
          val avgAmount   = value.df.select("avg_amount").first().getDecimal(0)

          ZIO.succeed(AmountModel(totalAmount, avgAmount))

        case None =>
          ZIO.fail(MetricNotFoundException("metric amount not found"))
      }
    } yield result
  }

  def getTopUsers: ZIO[SparkLive, SparkError, TopUsersModel] = {
    for {
      spark   <- ZIO.service[SparkLive]
      metrics <- spark.sendData()
      topUsers = metrics.find(_.name == "top_users")
      result <- topUsers match {
        case Some(value) =>
          val fio             = value.df.select("fio").first().getString(0)
          val email           = value.df.select("email").first().getString(0)
          val totalUserAmount = value.df.select("total_user_amount").first().getDecimal(0)

          ZIO.succeed(
            TopUsersModel(
              fio,
              email,
              totalUserAmount
            )
          )

        case None =>
          ZIO.fail(MetricNotFoundException("metric top_users not found"))
      }
    } yield result
  }

  def getCountryStats: ZIO[SparkLive, SparkError, CountryStatsModel] = {
    for {
      spark   <- ZIO.service[SparkLive]
      metrics <- spark.sendData()
      countryStats = metrics.find(_.name == "country_stats")
      result <- countryStats match {
        case Some(value) =>
          val country          = value.df.select("country").first().getString(0)
          val transactionCount = value.df.select("transaction_count").first().getLong(0)
          val totalAmount      = value.df.select("total_amount").first().getDecimal(0)

          ZIO.succeed(
            CountryStatsModel(
              country,
              transactionCount,
              totalAmount
            )
          )

        case None =>
          ZIO.fail(MetricNotFoundException("metric country_stats not found"))
      }
    } yield result
  }
}
