package exception

import zio.schema.{DeriveSchema, Schema}

sealed trait AppError

object AppError {

  case class InternalDatabaseException(message: String) extends AppError

  case class SparkReadException(message: String) extends AppError

  case class SparkCalculateException(message: String) extends AppError

  case class SparkSaveException(message: String) extends AppError

  case class MetricNotFoundException(message: String) extends AppError

  case class PasswordMismatchException(message: String) extends AppError

  case class AdminNotFoundException(message: String) extends AppError

  case class InternalException(message: String) extends AppError

  case class ClaimMissing() extends AppError

  case class InvalidToken() extends AppError

  object InternalDatabaseException {
    implicit val schema: Schema[InternalDatabaseException] = DeriveSchema.gen
  }

  object SparkReadException {
    implicit val schema: Schema[SparkReadException] = DeriveSchema.gen
  }

  object SparkCalculateException {
    implicit val schema: Schema[SparkCalculateException] = DeriveSchema.gen
  }

  object SparkSaveException {
    implicit val schema: Schema[SparkSaveException] = DeriveSchema.gen
  }

  object MetricNotFoundException {
    implicit val schema: Schema[MetricNotFoundException] = DeriveSchema.gen
  }

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
