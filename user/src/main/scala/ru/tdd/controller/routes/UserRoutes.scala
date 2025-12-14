package ru.tdd.controller.routes

import ru.tdd.app.models.dto.UpdateUserDto._
import ru.tdd.app.models.dto.{ExceptionDto, UpdateUserDto}
import ru.tdd.app.models.enums.Role
import ru.tdd.app.models.exceptions.{ApiException, AuthorizationException, ValidationException}
import ru.tdd.app.services.auth.UserServiceImp
import ru.tdd.controller.configs.JwtConfig
import ru.tdd.controller.routes.RoutesHandlers._
import ru.tdd.controller.security.Principal
import slick.jdbc.PostgresProfile.api._
import zio.ZIO
import zio.http.{Method, Request, Response, Routes, Status, handler, long, uuid, withContext}
import zio.json._

import java.time.{LocalDate, LocalDateTime}
import java.util.UUID

/**
 * @author Tribushko Danil
 * @since 20.11.2025
 *        Роуты для работы с пользователями
 */
object UserRoutes {

  private def validateUserOwnership(id: UUID, principal: Principal) =
    ZIO.when(
      id != principal.id && !principal.roles.contains(Role.Admin)
    )(
      ZIO.fail(AuthorizationException("Пользователь путается изменить другого пользователя"))
    )

  final def apply(db: Database, conf: JwtConfig): Routes[Any, Response] = {
    val service = new UserServiceImp(db)

    Routes(
      Method.PUT / "users" / uuid("id") ->
        handler { (id: UUID, req: Request) =>
          withContext { (principal: Principal) =>
            (for {
              _ <- validateUserOwnership(id, principal)
              body <- req.body.asString
              dto <- ZIO.fromEither(body.fromJson[UpdateUserDto]).mapError(ValidationException(_))
              updatedUser <- service.update(id, dto)
            } yield Response.json(updatedUser.toJson).status(Status.Ok)).catchAll {
              case ex: ApiException => ZIO.fail(Response.json(ExceptionDto(ex).toJson).status(ex.statusCode))
            }
          }
        },
      Method.GET / "users" / uuid("id") ->
        handler { (id: UUID, _: Request) =>
          withContext { (_: Principal) =>
            service.findById(id)
              .map(u => Response.json(u.toJson).status(Status.Ok))
              .catchAll {
                case ex: ApiException =>
                  ZIO.succeed(Response.json(ExceptionDto(ex).toJson).status(ex.statusCode))
                case ex: Throwable =>
                  ZIO.succeed(Response.text(s"Internal server error: ${ex.getMessage}").status(Status.InternalServerError))
              }
          }
        },
      Method.GET / "users" / "all" ->
        handler { req: Request =>
          withContext { (_: Principal) =>
            service.findAll(
                req.queryParam("username"),
                req.query[LocalDate]("birthday-start") match {
                  case Left(_) => None
                  case Right(date) => Option(date)
                },
                req.query[LocalDate]("birthday-end") match {
                  case Left(_) => None
                  case Right(date) => Option(date)
                },
                req.query[LocalDateTime]("creation-time-start") match {
                  case Left(_) => None
                  case Right(date) => Option(date)
                },
                req.query[LocalDateTime]("creation-time-end") match {
                  case Left(_) => None
                  case Right(date) => Option(date)
                },
                req.query[LocalDateTime]("update-time-start") match {
                  case Left(_) => None
                  case Right(date) => Option(date)
                },
                req.query[LocalDateTime]("update-time-end") match {
                  case Left(_) => None
                  case Right(date) => Option(date)
                }
              ).map(users => Response.json(users.toJson).status(Status.Ok))
              .mapError(ex => Response.text(ex.getMessage).status(Status.BadRequest))
          }
        },
      Method.DELETE / "users" / uuid("id") ->
        handler { (id: UUID, _: Request) =>
          withContext { (principal: Principal) =>
            validateUserOwnership(id, principal).flatMap(_ =>
              service.delete(id).map(_ => Response.status(Status.NoContent))
            ).catchAll {
              case ex: ApiException => ZIO.fail(Response.json(ExceptionDto(ex).toJson).status(ex.statusCode))
            }
          }
        },
      Method.GET / "users" ->
        handler {
          req: Request =>
            withContext { _: Principal =>
              req.query[Long]("chat-id") match {
                case Right(chatId) =>
                  service.getByChatId(chatId).map(dto => Response.json(dto.toJson).status(Status.Ok)).catchAll(ex =>
                    ZIO.succeed(Response.text(ex.getMessage).status(Status.BadRequest))
                  )
                case Left(_) =>
                  ZIO.succeed(Response.badRequest)
              }
            }
        }
    ) @@ jwtAuthHandler(db, conf, Seq(Role.User)) ++ Routes(
      Method.GET / "users" / long("chat-id") / "exists" ->
        handler {
          (chatId: Long, _: Request) =>
            service.isUserExistByChatId(chatId).map(dto => Response.json(dto.toJson).status(Status.Ok)).catchAll(ex =>
              ZIO.succeed(Response.text(ex.getMessage).status(Status.BadRequest)))
        },
      Method.GET / "users" / "exists" ->
        handler {
          req: Request =>
            req.queryParam("username").map { username =>
              service.isUserExistByUsername(username).map(dto => Response.json(dto.toJson).status(Status.Ok)).catchAll(ex =>
                ZIO.succeed(Response.text(ex.getMessage).status(Status.BadRequest)))
            }.getOrElse {
              ZIO.succeed(Response.status(Status.NotFound))
            }
        }
    )
  }
}