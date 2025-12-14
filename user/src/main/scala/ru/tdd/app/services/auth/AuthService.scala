package ru.tdd.app.services.auth

import ru.tdd.app.models.dto.{BooleanDTO, JwtTokenDto, SignInDto, SignUpDto}
import ru.tdd.app.models.enums.Role.User
import ru.tdd.app.models.exceptions.{AlreadyExistException, NotFoundException}
import ru.tdd.app.services.jwt.JwtServiceImp
import ru.tdd.controller.configs.JwtConfig
import ru.tdd.database.entities.users.AppUser
import ru.tdd.database.repositories.users.SlickUserRepository
import slick.jdbc.PostgresProfile.api._
import zio.{Task, ZIO}

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

/**
 * @author Tribushko Danil
 * @since 05.11.2025
 *        Сервис для регистрации и авторизации пользователей
 */
trait AuthService {

  def signIn(dto: SignInDto): Task[JwtTokenDto]

  def signUp(dto: SignUpDto): Task[JwtTokenDto]

}

/**
 * @author Tribushko Danil
 * @since 05.11.2025
 */
class AuthServiceImp(db: Database, conf: JwtConfig) extends AuthService {

  private val rep = new SlickUserRepository(db)

  private val jwtService = new JwtServiceImp(conf)

  override def signIn(dto: SignInDto): Task[JwtTokenDto] = {

    for {
      userOpt <-ZIO.fromFuture(implicit ex => rep.findOne(_.chatId === dto.chatId))
      user <- ZIO.fromOption(userOpt).mapError(_ => NotFoundException("Пользователь не найден"))
    } yield JwtTokenDto(jwtService.createToken(user))
  }

  override def signUp(dto: SignUpDto): Task[JwtTokenDto] = {

    for {
      isExist <- ZIO.fromFuture(_ => rep.existsByUsernameAndChatId(dto.username, dto.chatId))
      _ <- ZIO.when(isExist)(ZIO.fail(AlreadyExistException("Пользователь уже создан")))
      newUser = AppUser(UUID.randomUUID(), dto.username, dto.chatId, NOW_TIME, NOW_TIME, Seq(User), dto.birthday)
      _ <- ZIO.fromFuture(_ => rep.save(newUser))
    } yield JwtTokenDto(jwtService.createToken(newUser))
  }
}