package models

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

case class TopUsersModel(
    fio: String,
    email: String,
    totalUserAmount: BigDecimal
)

object TopUsersModel {
  implicit val codec: Codec[TopUsersModel] = deriveCodec[TopUsersModel]
}
