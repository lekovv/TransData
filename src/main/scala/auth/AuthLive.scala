package auth

import auth.AuthLive.{SECRET_TOKEN, checkPassword, jwtEncode}
import exception.AuthError
import exception.AuthError.{AdminNotFoundException, InternalException, PasswordMismatchException}
import io.circe.syntax.EncoderOps
import models.{Admin, JwtClaimData, Login}
import pdi.jwt.{JwtAlgorithm, JwtClaim, JwtZIOJson}
import service.admin.AdminRepo
import service.admin.AdminService.getAdmin
import zio.{ZIO, ZLayer}

import java.time.Instant
import java.util.UUID
import scala.util.Try

final case class AuthLive(admin: AdminRepo) {

  def authentication(login: Login): ZIO[AdminRepo, AuthError, String] = {
    for {
      admin <- getAdmin(login.username)
        .mapError(err => InternalException(err.getMessage))
      jwt <- admin match {
        case Some(admin) =>
          if (checkPassword(admin, login.password)) {
            ZIO.succeed(jwtEncode(admin.id, admin.username, SECRET_TOKEN))
          } else {
            ZIO.fail(PasswordMismatchException("password mismatch"))
          }

        case None => ZIO.fail(AdminNotFoundException("admin not found"))
      }

    } yield jwt
  }

}

object AuthLive {

  val SECRET_TOKEN     = "secret_token"
  private val TWO_DAYS = 172800

  def checkPassword(admin: Admin, password: String): Boolean =
    admin.password == password

  def jwtEncode(id: UUID, username: String, key: String): String =
    JwtZIOJson.encode(
      JwtClaim(
        expiration = Some(Instant.now.plusSeconds(TWO_DAYS).getEpochSecond),
        issuedAt = Some(Instant.now.getEpochSecond),
        subject = Some(JwtClaimData(id, username).asJson.noSpaces)
      ),
      key,
      JwtAlgorithm.HS256
    )

  def jwtDecode(token: String, key: String): Try[JwtClaim] =
    JwtZIOJson.decode(token, key, Seq(JwtAlgorithm.HS256))

  val layer = ZLayer.fromZIO {
    for {
      admin <- ZIO.service[AdminRepo]
    } yield AuthLive(admin)
  }
}
