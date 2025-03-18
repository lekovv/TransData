package exception

import zio.schema.{DeriveSchema, Schema}

sealed trait SparkError

object SparkError {

  case class SparkReadException(message: String) extends SparkError

  case class SparkCalculateException(message: String) extends SparkError

  case class SparkSaveException(message: String) extends SparkError

  case class MetricNotFoundException(message: String) extends SparkError

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
}
