package service.transaction

import exception.Exceptions.InternalDatabaseException
import io.getquill.{PostgresZioJdbcContext, SnakeCase}
import models.{TransactionsModel, TransactionsRequest}
import zio.{Task, ZIO, ZLayer}

import java.time.LocalDateTime
import java.util.UUID
import javax.sql.DataSource

final case class TransactionRepoLive(ds: DataSource) extends TransactionRepo {

  private val dsZL = ZLayer.succeed(ds)

  private val ctx = new PostgresZioJdbcContext(SnakeCase)

  import ctx._

  private val transSchema = quote {
    querySchema[TransactionsModel]("public.transaction")
  }

  override def createTransactions(transactionsRequest: List[TransactionsRequest]): Task[List[UUID]] = {

    val transactionInserts = transactionsRequest.map(transactionRequest => {
      val id      = UUID.randomUUID()
      val created = LocalDateTime.now()

      val transaction = TransactionsModel(
        id,
        transactionRequest.userId,
        transactionRequest.amount,
        transactionRequest.transactionType,
        transactionRequest.description,
        created
      )

      ctx
        .run(transSchema.insertValue(lift(transaction)).returning(_.id))
        .mapBoth(
          err => InternalDatabaseException(err.getMessage),
          _ => id
        )
    })

    ZIO.collectAll(transactionInserts).provide(dsZL)
  }
}

object TransactionRepoLive {
  val layer = ZLayer.fromZIO {
    for {
      ds <- ZIO.service[DataSource]
    } yield TransactionRepoLive(ds)
  }
}
