package models

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

case class CountryStatsModel(
    country: String,
    transactionCount: Long,
    totalAmount: BigDecimal
)

object CountryStatsModel {
  implicit val codec: Codec[CountryStatsModel] = deriveCodec[CountryStatsModel]
}
