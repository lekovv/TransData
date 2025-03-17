package models

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

case class AmountModel(
    totalAmount: BigDecimal,
    avgAmount: BigDecimal
)

object AmountModel {
  implicit val codec: Codec[AmountModel] = deriveCodec[AmountModel]
}
