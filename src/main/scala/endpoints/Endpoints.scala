package endpoints

import auth.AuthLive
import auth.AuthLive.bearerAuthMiddleware
import auth.AuthService.authentication
import exception.AppError
import exception.AppError._
import models._
import service.admin.AdminRepo
import service.spark.SparkLive
import service.spark.SparkService.{getAmountMetric, getCountryStats, getTopUsers}
import service.transaction.TransactionRepo
import service.transaction.TransactionService.createTransactions
import service.user.UserRepo
import service.user.UserService._
import zio.http.codec.HttpCodec
import zio.http.endpoint.Endpoint
import zio.http.{RoutePattern, Routes, Status}
import zio.schema.DeriveSchema.gen

import java.util.UUID

object Endpoints {

  private val createUserAPI =
    Endpoint(RoutePattern.POST / "api" / "create" / "users")
      .in[List[UserRequest]]
      .out[List[UUID]](Status.Created)
      .outError[InternalDatabaseException](Status.InternalServerError)

  private val createTransactionsAPI =
    Endpoint(RoutePattern.POST / "api" / "create" / "transactions")
      .in[List[TransactionsRequest]]
      .out[List[UUID]]
      .outError[InternalDatabaseException](Status.InternalServerError)

  private val getAmountMetricAPI =
    Endpoint(RoutePattern.GET / "api" / "spark" / "amount")
      .out[AmountModel]
      .outErrors[AppError](
        HttpCodec.error[SparkReadException](Status.InternalServerError),
        HttpCodec.error[SparkCalculateException](Status.InternalServerError),
        HttpCodec.error[SparkSaveException](Status.InternalServerError),
        HttpCodec.error[MetricNotFoundException](Status.NotFound)
      )

  private val getTopUsersMetricAPI =
    Endpoint(RoutePattern.GET / "api" / "spark" / "top-users")
      .out[List[TopUsersModel]]
      .outErrors[AppError](
        HttpCodec.error[SparkReadException](Status.InternalServerError),
        HttpCodec.error[SparkCalculateException](Status.InternalServerError),
        HttpCodec.error[SparkSaveException](Status.InternalServerError),
        HttpCodec.error[MetricNotFoundException](Status.NotFound)
      )

  private val getCountryStatsAPI =
    Endpoint(RoutePattern.GET / "api" / "spark" / "country-stats")
      .out[List[CountryStatsModel]]
      .outErrors[AppError](
        HttpCodec.error[SparkReadException](Status.InternalServerError),
        HttpCodec.error[SparkCalculateException](Status.InternalServerError),
        HttpCodec.error[SparkSaveException](Status.InternalServerError),
        HttpCodec.error[MetricNotFoundException](Status.NotFound)
      )

  private val loginAPI =
    Endpoint(RoutePattern.POST / "login")
      .in[Login]
      .out[String]
      .outErrors[AppError](
        HttpCodec.error[InternalException](Status.Unauthorized),
        HttpCodec.error[AdminNotFoundException](Status.Unauthorized),
        HttpCodec.error[PasswordMismatchException](Status.Unauthorized)
      )

  val loginRoute: Routes[AdminRepo with AuthLive, Nothing] = Routes(
    loginAPI.implement { login =>
      authentication(login)
    }
  )

  val routes: Routes[UserRepo with TransactionRepo with SparkLive with AuthLive with AdminRepo, Nothing] = Routes(
    createUserAPI.implement(req => createUser(req)),
    createTransactionsAPI.implement(req => createTransactions(req)),
    getAmountMetricAPI.implement(_ => getAmountMetric),
    getTopUsersMetricAPI.implement(_ => getTopUsers),
    getCountryStatsAPI.implement(_ => getCountryStats)
    // TODO: настроить авторизацию через валидацию заголовка
  ) @@ bearerAuthMiddleware
}
