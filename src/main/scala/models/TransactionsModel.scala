package models

import io.circe.generic.semiauto.deriveCodec
import io.circe.{Codec, Decoder, Encoder}

import java.time.LocalDateTime
import java.util.UUID

case class TransactionsModel(
    id: UUID,
    userId: UUID,
    amount: BigDecimal,
    currency: String,
    transactionType: String,
    description: Option[String],
    created: LocalDateTime
)

object TransactionsModel {
  implicit val codec: Codec[TransactionsModel]          = deriveCodec[TransactionsModel]
  implicit val uuidCodec: Codec[UUID]                   = Codec.from(Decoder.decodeString.map(UUID.fromString), Encoder.encodeString.contramap(_.toString))
  implicit val localDateTimeCodec: Codec[LocalDateTime] = Codec.from(Decoder.decodeString.map(LocalDateTime.parse), Encoder.encodeString.contramap(_.toString))
}
