package service.transaction

import models.TransactionsRequest
import zio.Task
import zio.macros.accessible

import java.util.UUID

@accessible
trait TransactionRepo {

  def createTransactions(transactionsRequest: List[TransactionsRequest]): Task[List[UUID]]
}

object TransactionRepo {
  val live = TransactionRepoLive.layer
}
