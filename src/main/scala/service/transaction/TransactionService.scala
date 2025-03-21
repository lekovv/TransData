package service.transaction

import exception.AppError.InternalDatabaseException
import models.TransactionsRequest
import zio.ZIO

import java.util.UUID

object TransactionService {

  def createTransactions(transactionsRequest: List[TransactionsRequest]): ZIO[TransactionRepo, InternalDatabaseException, List[UUID]] =
    for {
      service <- ZIO.service[TransactionRepo]
      result  <- service.createTransactions(transactionsRequest)
    } yield result
}
