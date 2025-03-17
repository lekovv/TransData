package models

import io.circe.generic.semiauto.deriveCodec
import io.circe.{Codec, Decoder, Encoder}

import java.util.UUID

case class TransactionsRequest(
    userId: UUID,
    amount: BigDecimal,
    transactionType: String,
    description: Option[String]
)

object TransactionsRequest {
  implicit val codec: Codec[TransactionsRequest] = deriveCodec[TransactionsRequest]
  implicit val uuidCodec: Codec[UUID]            = Codec.from(Decoder.decodeString.map(UUID.fromString), Encoder.encodeString.contramap(_.toString))
}
