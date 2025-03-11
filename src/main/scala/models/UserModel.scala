package models

import io.circe.generic.semiauto.deriveCodec
import io.circe.{Codec, Decoder, Encoder}

import java.time.LocalDateTime
import java.util.UUID

case class UserModel(
    id: UUID,
    email: String,
    firstName: String,
    lastName: String,
    country: String,
    created: LocalDateTime
)

object UserModel {
  implicit val codec: Codec[UserModel]                  = deriveCodec[UserModel]
  implicit val uuidCodec: Codec[UUID]                   = Codec.from(Decoder.decodeString.map(UUID.fromString), Encoder.encodeString.contramap(_.toString))
  implicit val localDateTimeCodec: Codec[LocalDateTime] = Codec.from(Decoder.decodeString.map(LocalDateTime.parse), Encoder.encodeString.contramap(_.toString))
}
