package endpoints

import exception.Exceptions.{InternalDatabaseException, MetricNotFoundException}
import models.{AmountModel, CountryStatsModel, TopUsersModel, TransactionsRequest, UserRequest}
import service.spark.SparkService.{getAmountMetric, getCountryStats, getTopUsers}
import service.transaction.TransactionService.createTransactions
import service.user.UserService._
import zio.http.endpoint.Endpoint
import zio.http.{RoutePattern, Routes, Status}
import zio.schema.DeriveSchema.gen

import java.util.UUID

object Endpoints {

  private val createUserAPI =
    Endpoint(RoutePattern.POST / "api" / "create" / "user")
      .in[List[UserRequest]]
      .out[List[UUID]](Status.Created)
      .outError[InternalDatabaseException](Status.InternalServerError)

  private val createUserRoute = {
    createUserAPI.implement(req =>
      createUser(req).mapBoth(
        err => InternalDatabaseException(err.getMessage),
        id => id
      )
    )
  }

  private val createTransactionsAPI =
    Endpoint(RoutePattern.POST / "api" / "create" / "transactions")
      .in[List[TransactionsRequest]]
      .out[List[UUID]]
      .outError[InternalDatabaseException](Status.InternalServerError)

  private val createTransactionsRoute =
    createTransactionsAPI.implement(req =>
      createTransactions(req).mapBoth(
        err => InternalDatabaseException(err.getMessage),
        id => id
      )
    )

  private val getAmountMetricAPI =
    Endpoint(RoutePattern.GET / "api" / "spark" / "amount")
      .out[AmountModel]
      .outError[MetricNotFoundException](Status.NotFound)

  private val getAmountMetricRoute =
    getAmountMetricAPI.implement(_ =>
      getAmountMetric
        .mapError(err => MetricNotFoundException(err.getMessage))
    )

  private val getTopUsersMetricAPI =
    Endpoint(RoutePattern.GET / "api" / "spark" / "top-users")
      .out[TopUsersModel]
      .outError[MetricNotFoundException](Status.NotFound)

  private val getTopUsersMetricRoute =
    getTopUsersMetricAPI.implement(_ =>
      getTopUsers
        .mapError(err => MetricNotFoundException(err.getMessage))
    )

  private val getCountryStatsAPI =
    Endpoint(RoutePattern.GET / "api" / "spark" / "country-stats")
      .out[CountryStatsModel]
      .outError[MetricNotFoundException](Status.NotFound)

  private val getCountryStatsRoute =
    getCountryStatsAPI.implement(_ =>
      getCountryStats
        .mapError(err => MetricNotFoundException(err.getMessage))
    )

  val routes =
    Routes(
      createUserRoute,
      createTransactionsRoute,
      getAmountMetricRoute,
      getTopUsersMetricRoute,
      getCountryStatsRoute
    )
}
