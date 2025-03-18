package exception

import zio.schema.{DeriveSchema, Schema}

sealed trait AuthError

object AuthError {

  final case class PasswordMismatchException(message: String) extends AuthError

  object PasswordMismatchException {
    implicit val schema: Schema[PasswordMismatchException] = DeriveSchema.gen
  }

  final case class AdminNotFoundException(message: String) extends AuthError

  object AdminNotFoundException {
    implicit val schema: Schema[AdminNotFoundException] = DeriveSchema.gen
  }

  final case class InternalException(message: String) extends AuthError

  object InternalException {
    implicit val schema: Schema[InternalException] = DeriveSchema.gen
  }

  case class ClaimMissing() extends AuthError

  object ClaimMissing {
    implicit val schema: Schema[ClaimMissing] = DeriveSchema.gen
  }

  case class InvalidToken() extends AuthError

  object InvalidToken {
    implicit val schema: Schema[ClaimMissing] = DeriveSchema.gen
  }
}
