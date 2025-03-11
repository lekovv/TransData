package endpoints

import exception.Exceptions.InternalDatabaseException
import models.{TransactionsRequest, UserRequest}
import service.transaction.TransactionService.createTransactions
import service.user.UserService._
import zio.http.endpoint.Endpoint
import zio.http.{RoutePattern, Routes, Status}
import zio.schema.DeriveSchema.gen

import java.util.UUID

object Endpoints {

  private val createUserAPI =
    Endpoint(RoutePattern.POST / "api" / "create" / "user")
      .in[UserRequest]
      .out[UUID](Status.Created)
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

  val routes =
    Routes(
      createUserRoute,
      createTransactionsRoute
    )
}
