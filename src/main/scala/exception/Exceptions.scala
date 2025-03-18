package exception

import zio.schema.{DeriveSchema, Schema}

object Exceptions {

  case class InternalDatabaseException(message: String) extends Exception(message)

  object InternalDatabaseException {
    implicit val schema: Schema[InternalDatabaseException] = DeriveSchema.gen
  }
}
