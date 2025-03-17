package models

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import zio.schema.{DeriveSchema, Schema}

import java.util.UUID

case class Admin(id: UUID, username: String, password: String)

object Admin {
  implicit val schema: Schema[Admin] = DeriveSchema.gen[Admin]
  implicit val codec: Codec[Admin]   = deriveCodec[Admin]
}
