package exception

import zio.schema.{DeriveSchema, Schema}

object Exceptions {

  case class UserNotFoundException(message: String) extends Exception(message)

  case class InternalDatabaseException(message: String) extends Exception(message)

  case class BodyParsingException(message: String) extends Exception(message)

  case class InternalException(message: String) extends Exception(message)

  object StatusNotFoundException {
    implicit val schema: Schema[UserNotFoundException] = DeriveSchema.gen
  }

  object InternalDatabaseException {
    implicit val schema: Schema[InternalDatabaseException] = DeriveSchema.gen
  }

  object BodyParsingException {
    implicit val schema: Schema[BodyParsingException] = DeriveSchema.gen
  }

  object InternalException {
    implicit val schema: Schema[InternalException] = DeriveSchema.gen
  }
}
