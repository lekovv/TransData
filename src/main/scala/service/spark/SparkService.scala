package service.spark

import exception.AppError
import exception.AppError.MetricNotFoundException
import models.{AmountModel, CountryStatsModel, TopUsersModel}
import zio.ZIO

object SparkService {

  def getAmountMetric: ZIO[SparkLive, AppError, AmountModel] = {
    for {
      spark   <- ZIO.service[SparkLive]
      metrics <- spark.sendData()
      amount = metrics.find(_.name == "amount")
      result <- amount match {
        case Some(value) =>
          val amountRows = value.df.collect()

          val totalAmount = amountRows(0).getDecimal(0)
          val avgAmount   = amountRows(0).getDecimal(1)

          ZIO.succeed(AmountModel(totalAmount, avgAmount))

        case None =>
          ZIO.fail(MetricNotFoundException("metric amount not found"))
      }
    } yield result
  }

  def getTopUsers: ZIO[SparkLive, AppError, List[TopUsersModel]] = {
    for {
      spark   <- ZIO.service[SparkLive]
      metrics <- spark.sendData()
      topUsers = metrics.find(_.name == "top_users")
      result <- topUsers match {
        case Some(value) =>
          val userRows = value.df.collect()

          val users = userRows
            .map(row =>
              TopUsersModel(
                row.getString(0),
                row.getString(1),
                row.getDecimal(2)
              )
            )
            .toList

          ZIO.succeed(users)

        case None =>
          ZIO.fail(MetricNotFoundException("metric top_users not found"))
      }
    } yield result
  }

  def getCountryStats: ZIO[SparkLive, AppError, List[CountryStatsModel]] = {
    for {
      spark   <- ZIO.service[SparkLive]
      metrics <- spark.sendData()
      countryStats = metrics.find(_.name == "country_stats")
      result <- countryStats match {
        case Some(value) =>
          val countryRows = value.df.collect()

          val countries = countryRows
            .map(row =>
              CountryStatsModel(
                row.getString(0),
                row.getLong(1),
                row.getDecimal(2)
              )
            )
            .toList

          ZIO.succeed(countries)

        case None =>
          ZIO.fail(MetricNotFoundException("metric country_stats not found"))
      }
    } yield result
  }
}
