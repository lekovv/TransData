package models

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import zio.schema.{DeriveSchema, Schema}

case class Login(username: String, password: String)

object Login {
  implicit val schema: Schema[Login] = DeriveSchema.gen[Login]
  implicit val codec: Codec[Login] = deriveCodec[Login]
}
