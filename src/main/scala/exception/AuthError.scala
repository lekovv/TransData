package exception

import zio.schema.{DeriveSchema, Schema}

sealed trait AuthError

object AuthError {

  case class PasswordMismatchException(message: String) extends AuthError

  case class AdminNotFoundException(message: String) extends AuthError

  case class InternalException(message: String) extends AuthError

  case class ClaimMissing() extends AuthError

  case class InvalidToken() extends AuthError

  object ClaimMissing {
    implicit val schema: Schema[ClaimMissing] = DeriveSchema.gen
  }

  object InternalException {
    implicit val schema: Schema[InternalException] = DeriveSchema.gen
  }

  object AdminNotFoundException {
    implicit val schema: Schema[AdminNotFoundException] = DeriveSchema.gen
  }

  object PasswordMismatchException {
    implicit val schema: Schema[PasswordMismatchException] = DeriveSchema.gen
  }

  object InvalidToken {
    implicit val schema: Schema[ClaimMissing] = DeriveSchema.gen
  }
}
