package service.transaction

import exception.AppError.InternalDatabaseException
import models.TransactionsRequest
import zio.macros.accessible
import zio.{IO, URLayer}

import java.util.UUID
import javax.sql.DataSource

@accessible
trait TransactionRepo {

  def createTransactions(transactionsRequest: List[TransactionsRequest]): IO[InternalDatabaseException, List[UUID]]
}

object TransactionRepo {
  val live: URLayer[DataSource, TransactionRepoLive] = TransactionRepoLive.layer
}
