package models

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

case class UserRequest(
    email: String,
    firstName: String,
    lastName: String,
    country: String
)

object UserRequest {
  implicit val codec: Codec[UserRequest] = deriveCodec[UserRequest]
}
