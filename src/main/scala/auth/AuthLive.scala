package auth

import auth.AuthLive.{checkPassword, jwtEncode, SECRET_TOKEN}
import exception.AuthError
import exception.AuthError._
import io.circe.syntax.EncoderOps
import models.{Admin, JwtClaimData, Login}
import pdi.jwt.{JwtAlgorithm, JwtClaim, JwtZIOJson}
import service.admin.AdminRepo
import zio.http.{Handler, HandlerAspect, Header, Headers, Request, Response}
import zio.{IO, ZIO, ZLayer}

import java.sql.SQLException
import java.time.Instant
import java.util.UUID
import scala.util.Try

final case class AuthLive(admin: AdminRepo) {

  def authentication(login: Login): IO[AuthError, String] = {
    for {
      admin <- admin
        .getAdmin(login.username)
        .mapError { _: SQLException => InternalException("database error") }
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

//  def processJwt(token: String): ZIO[Any, AuthError, Unit] =
//    for {
//      claim <- ZIO.fromTry(jwtDecode(token, SECRET_TOKEN)).orElseFail(InvalidToken())
//      _     <- ZIO.fromOption(claim.subject).orElseFail(ClaimMissing())
//    } yield ()
}

object AuthLive {

  val SECRET_TOKEN     = "secret_token"
  private val TWO_DAYS = 172800

  val bearerAuthMiddleware: HandlerAspect[Any, String] =
    HandlerAspect.interceptIncomingHandler(Handler.fromFunctionZIO[Request] { req =>
      req.header(Header.Authorization) match {
        case Some(Header.Authorization.Bearer(token)) =>
          ZIO
            .fromTry(jwtDecode(token.value.asString, SECRET_TOKEN))
            .mapError(_ => InvalidToken)
            .flatMap(claim =>
              ZIO
                .fromOption(claim.subject)
                .mapError(_ => ClaimMissing)
            )
            .either
            .flatMap {
              case Right(user)        => ZIO.succeed((req, user))
              case Left(InvalidToken) => ZIO.fail(Response.unauthorized("invalid token"))
              case Left(ClaimMissing) => ZIO.fail(Response.unauthorized("subject claim mismatch"))
            }
        case _ =>
          ZIO.fail(Response.unauthorized.addHeaders(Headers(Header.WWWAuthenticate.Bearer(realm = "Access"))))
      }
    })

  private def checkPassword(admin: Admin, password: String): Boolean =
    admin.password == password

  private def jwtEncode(id: UUID, username: String, key: String): String =
    JwtZIOJson.encode(
      JwtClaim(
        expiration = Some(Instant.now.plusSeconds(TWO_DAYS).getEpochSecond),
        issuedAt = Some(Instant.now.getEpochSecond),
        subject = Some(JwtClaimData(id, username).asJson.noSpaces)
      ),
      key,
      JwtAlgorithm.HS256
    )

  private def jwtDecode(token: String, key: String): Try[JwtClaim] =
    JwtZIOJson.decode(token, key, Seq(JwtAlgorithm.HS256))

  val layer = ZLayer.fromZIO {
    for {
      admin <- ZIO.service[AdminRepo]
    } yield AuthLive(admin)
  }
}
