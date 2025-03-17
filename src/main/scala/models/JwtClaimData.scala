package models

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

import java.util.UUID


case class JwtClaimData(id: UUID, username: String)

object JwtClaimData {
  implicit val codec: Codec[JwtClaimData] = deriveCodec[JwtClaimData]
}
