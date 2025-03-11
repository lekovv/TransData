package service.transaction

import models.TransactionsRequest
import zio.{RIO, ZIO}

import java.util.UUID

object TransactionService {

  def createTransactions(transactionsRequest: List[TransactionsRequest]): RIO[TransactionRepo, List[UUID]] =
    for {
      service <- ZIO.service[TransactionRepo]
      result  <- service.createTransactions(transactionsRequest)
    } yield result
}
