package exception

import zio.schema.{DeriveSchema, Schema}

object Exceptions {

  case class MetricNotFoundException(message: String) extends Exception(message)

  case class InternalDatabaseException(message: String) extends Exception(message)

  case class SparkReadException(message: String) extends Exception(message)

  case class SparkCalculateException(message: String) extends Exception(message)

  object MetricNotFoundException {
    implicit val schema: Schema[MetricNotFoundException] = DeriveSchema.gen
  }

  object InternalDatabaseException {
    implicit val schema: Schema[InternalDatabaseException] = DeriveSchema.gen
  }

  object SparkReadException {
    implicit val schema: Schema[SparkReadException] = DeriveSchema.gen
  }

  object SparkCalculateException {
    implicit val schema: Schema[SparkCalculateException] = DeriveSchema.gen
  }
}
