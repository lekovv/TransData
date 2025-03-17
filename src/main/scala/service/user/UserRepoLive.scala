package service.user

import exception.Exceptions.InternalDatabaseException
import io.getquill.{PostgresZioJdbcContext, SnakeCase}
import models.{UserModel, UserRequest}
import zio.{Task, ZIO, ZLayer}

import java.time.LocalDateTime
import java.util.UUID
import javax.sql.DataSource

final case class UserRepoLive(ds: DataSource) extends UserRepo {

  private val dsZL = ZLayer.succeed(ds)

  private val ctx = new PostgresZioJdbcContext(SnakeCase)

  import ctx._

  private val userSchema = quote {
    querySchema[UserModel]("public.user")
  }

  override def createUser(usersRequest: List[UserRequest]): Task[List[UUID]] = {

    val userInserts = usersRequest.map(userRequest => {
      val id      = UUID.randomUUID()
      val created = LocalDateTime.now()

      val user = UserModel(
        id,
        userRequest.email,
        userRequest.firstName,
        userRequest.lastName,
        userRequest.country,
        created
      )

      ctx
        .run(userSchema.insertValue(lift(user)).returning(_.id))
        .mapBoth(
          err => InternalDatabaseException(err.getMessage),
          _ => id
        )
    })

    ZIO.collectAll(userInserts).provide(dsZL)
  }
}

object UserRepoLive {
  val layer = ZLayer.fromZIO {
    for {
      ds <- ZIO.service[DataSource]
    } yield UserRepoLive(ds)
  }
}
