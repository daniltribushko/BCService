package ru.tdd.controller.routes

import ru.tdd.app.models.dto.{ExceptionDto, SignInDto, SignUpDto}
import ru.tdd.app.models.exceptions.{ApiException, BadRequestException}
import ru.tdd.app.services.auth.AuthServiceImp
import ru.tdd.controller.configs.JwtConfig
import slick.jdbc.PostgresProfile.api._
import zio.ZIO
import zio.http.{Method, Request, Response, Routes, Status, handler}
import zio.json._

/**
 * @author Tribushko Danil
 * @since 05.11.2025
 *        Маршруты для авторизации пользователей
 */
object AuthRoutes {

  def apply(db: Database, conf: JwtConfig): Routes[Any, Response] = {

    val authService = new AuthServiceImp(db, conf)
    Routes(
      Method.POST / "users" / "sign-up" ->
        handler {
          (req: Request) =>
            (for {
              body <- req.body.asString
              dto <- ZIO.fromEither(body.fromJson[SignUpDto])
                .mapError(BadRequestException(_))
              token <- authService.signUp(dto)
            } yield Response.json(token.toJson).status(Status.Created)).catchAll {
              case apiEx: ApiException =>
                ZIO.succeed(Response.json(ExceptionDto(apiEx).toJson).status(apiEx.statusCode))
              case ex: Throwable =>
                ZIO.succeed(Response.text(ex.getMessage).status(Status.BadRequest))
            }
        },
      Method.POST / "users" / "sign-in" ->
        handler {
          req: Request =>
            (for {
              body <- req.body.asString
              dto <- ZIO.fromEither(body.fromJson[SignInDto])
                .mapError(BadRequestException(_))
              token <- authService.signIn(dto)
            } yield Response.json(token.toJson).status(Status.Ok)).catchAll {
              case apiEx: ApiException =>
                ZIO.succeed(Response.json(ExceptionDto(apiEx).toJson).status(apiEx.statusCode))
              case ex: Throwable =>
                ZIO.succeed(Response.text(ex.getMessage).status(Status.BadRequest))
            }
        }
    )
  }
}
