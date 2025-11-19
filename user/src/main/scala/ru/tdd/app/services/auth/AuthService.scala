package ru.tdd.app.services.auth

import ru.tdd.app.models.dto.{JwtTokenDto, SignInDto, SignUpDto}
import ru.tdd.app.models.enums.Role.User
import ru.tdd.app.models.exceptions.{AlreadyExistException, NotFoundException}
import ru.tdd.app.services.jwt.JwtServiceImp
import ru.tdd.controller.configs.JwtConfig
import ru.tdd.database.entities.users.AppUser
import ru.tdd.database.repositories.users.SlickUserRepository
import slick.jdbc.PostgresProfile.api._

import java.util.UUID
import scala.concurrent.Future

/**
 * @author Tribushko Danil
 * @since 05.11.2025
 *        Сервис для регистрации и авторизации пользователей
 */
trait AuthService {

  def signIn(dto: SignInDto): Future[JwtTokenDto]

  def signUp(dto: SignUpDto): Future[JwtTokenDto]
}

/**
 * @author Tribushko Danil
 * @since 05.11.2025
 */
class AuthServiceImp(db: Database, conf: JwtConfig) extends AuthService {

  private val rep = new SlickUserRepository(db)

  private val jwtService = new JwtServiceImp(conf)

  override def signIn(dto: SignInDto): Future[JwtTokenDto] = {
    import scala.concurrent.ExecutionContext

    implicit val ec: ExecutionContext = ExecutionContext.global

    for {
      userOpt <- rep.findOne(u => u.username === dto.username && u.chatId === dto.chatId)
    } yield {
      userOpt match {
        case Some(user) => JwtTokenDto(jwtService.createToken(user))
        case _ => throw NotFoundException("Пользователь не найден")
      }
    }
  }

  override def signUp(dto: SignUpDto): Future[JwtTokenDto] = {
    import scala.concurrent.ExecutionContext

    implicit val ec: ExecutionContext = ExecutionContext.global

    for {
      isExist <- rep.existsByUsernameAndChatId(dto.username, dto.chatId)
    } yield {
      if (!isExist) {
        val newUser = AppUser(UUID.randomUUID(), dto.username, dto.chatId, NOW_TIME, NOW_TIME, Seq(User), dto.birthday)
        rep.save(newUser)
        JwtTokenDto(jwtService.createToken(newUser))
      } else {
        throw AlreadyExistException("Пользователь уже создан")
      }
    }
  }
}