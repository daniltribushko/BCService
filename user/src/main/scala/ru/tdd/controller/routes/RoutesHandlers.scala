package ru.tdd.controller.routes

import ru.tdd.app.models.dto.ExceptionDto
import ru.tdd.app.models.enums.Role
import ru.tdd.app.models.exceptions.{ApiException, AuthorizationException, NotFoundException}
import ru.tdd.app.services.jwt.JwtServiceImp
import ru.tdd.controller.configs.JwtConfig
import ru.tdd.controller.security.Principal
import ru.tdd.database.repositories.users.SlickUserRepository
import slick.jdbc.PostgresProfile.api._
import zio.ZIO
import zio.http.{Handler, HandlerAspect, Header, Request, Response, Status}
import zio.json.EncoderOps

import java.util.UUID

object RoutesHandlers {

  private final def getToken(req: Request) = ZIO.fromOption(req.header(Header.Authorization).collect {
    case Header.Authorization.Bearer(token) => token.value.asString
  }).orElseFail(Response.status(Status.Unauthorized))

  final def jwtAuthHandler(db: Database, conf: JwtConfig, roles: Seq[Role]): HandlerAspect[Any, Principal] = {

    HandlerAspect.interceptIncomingHandler(Handler.fromFunctionZIO[Request] { req =>
      val userRep = new SlickUserRepository(db)
      (for {
        token <- getToken(req)

        jwtService = new JwtServiceImp(conf)
        _ <- ZIO.when(!jwtService.validateToken(token))(
          ZIO.fail(AuthorizationException("Токен не валидный"))
        )

        claims = jwtService.parseToken(token)

        userId <- ZIO.attempt(UUID.fromString(claims.get("id").toString))
          .mapError(ex => new RuntimeException(ex.getMessage))

        userOpt <- ZIO.fromFuture(_ => userRep.findById(userId))
        user <- ZIO.fromOption(userOpt).mapError(_ => NotFoundException("Пользователь не найден"))

        userRoles = user.roles

        _ <- ZIO.when(!roles.exists(userRoles.contains))(
          ZIO.fail(AuthorizationException("У пользователя нет доступа"))
        )
      } yield (req, Principal(userId, user.username, userRoles))).catchAll {
        case apiEx: ApiException =>
          ZIO.fail(Response.json(ExceptionDto(apiEx).toJson).status(apiEx.statusCode))
        case ex: RuntimeException =>
          ZIO.fail(Response.text(ex.getMessage).status(Status.Forbidden))
      }
    })
  }
}
