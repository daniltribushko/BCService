package ru.tdd.app.services.auth

import ru.tdd.app.models.dto.{BooleanDTO, UpdateUserDto, UserDto}
import ru.tdd.app.models.exceptions.{AlreadyExistException, NotFoundException}
import ru.tdd.database.entities.users.AppUser
import ru.tdd.database.repositories.users.SlickUserRepository
import slick.jdbc.PostgresProfile.api._
import zio.{Task, ZIO}

import java.time.{LocalDate, LocalDateTime}
import java.util.UUID

/**
 * @author Tribushko Danil
 * @since 20.11.2025
 *        Сервис для работы с пользователями
 */
trait UserService {

  /**
   * Обновление пользователя
   */
  def update(userId: UUID, dto: UpdateUserDto): ZIOUserDTO

  /**
   * Удаление пользователя
   */
  def delete(userId: UUID): ZUnit

  /**
   * Получение списка пользователей с фильтрами
   * @param usernameOpt имя пользователя в телеграмм
   * @param birthdayStartOpt левая граница дня рождения
   * @param birthdayEndOpt правая граница дня рождения
   * @param creationTimeStartOpt левая граница даты создания
   * @param creationTimeEndOpt правая граница даты создания
   * @param updateTimeStartOpt левая граница даты последнего обновления
   * @param updateTimeEndOpt правая граница даты последнего обновления
   * @return
   */
  def findAll(
               usernameOpt: Option[String],
               birthdayStartOpt: Option[LocalDate],
               birthdayEndOpt: Option[LocalDate],
               creationTimeStartOpt: Option[LocalDateTime],
               creationTimeEndOpt: Option[LocalDateTime],
               updateTimeStartOpt: Option[LocalDateTime],
               updateTimeEndOpt: Option[LocalDateTime]
             ): ZIOUserDTOS

  /**
   * Поиск пользователя по его идентификатору
   */
  def findById(
                id: UUID
              ): ZIOUserDTO

  /**
   * Проверка наличия пользователя в базе данных
   */
  def isUserExistByChatId(chatId: Long): Task[BooleanDTO]

  /**
   * Проверка наличия пользователя по имени в базе данных
   */
  def isUserExistByUsername(username: String): Task[BooleanDTO]

  /**
   * Получение пользователя по идентификатору телеграм
   */
  def getByChatId(chatId: Long): ZIOUserDTO

  /**
   * Получение пользователя по его имени
   */
  def getByUsername(username: String): ZIOUserDTO
}

class UserServiceImp(db: Database) extends UserService {

  private val rep = new SlickUserRepository(db)

  override def update(userId: UUID, dto: UpdateUserDto): ZIOUserDTO = {
    for {
      userOpt <- ZIO.fromFuture(implicit ex => rep.findById(userId))
      user <- ZIO.fromOption(userOpt).mapError(_ => NotFoundException("Пользователь с указанным идентификатором не найден"))
      _ <- ZIO.when(dto.username.isDefined && user.username != dto.username.get) {
        ZIO.fromFuture(_ => rep.exists(_.username === dto.username.get)).flatMap {
          case true => ZIO.fail(AlreadyExistException("Пользователь с указанным именем телеграмма уже создан"))
          case false => ZIO.unit
        }
      }
      newUser = AppUser(
        userId,
        dto.username.getOrElse(user.username),
        user.chatId,
        user.creationTime,
        NOW_TIME,
        user.roles,
        dto.birthday.getOrElse(user.birthday)
      )
      _ <- ZIO.fromFuture(
        _ => rep.update(newUser)
      )
    } yield UserDto.fromEntity(newUser)
  }

  override def delete(userId: UUID): ZUnit =
    for {
      _ <- ZIO.fromFuture(_ => rep.exists(_.id === userId)).flatMap {
        case false => ZIO.fail(NotFoundException("Пользователь с указанным идентификатором не найден"))
        case true => ZIO.unit
      }
      _ <- ZIO.fromFuture(_ => rep.deleteById(userId))
    } yield {}

  override def findAll(
                        usernameOpt: Option[String] = None,
                        birthdayStartOpt: Option[LocalDate] = None,
                        birthdayEndOpt: Option[LocalDate] = None,
                        creationTimeStartOpt: Option[LocalDateTime] = None,
                        creationTimeEndOpt: Option[LocalDateTime] = None,
                        updateTimeStartOpt: Option[LocalDateTime] = None,
                        updateTimeEndOpt: Option[LocalDateTime] = None
                      ): ZIOUserDTOS =
    ZIO.fromFuture(implicit ex =>
      rep.findAll(u =>
        usernameOpt.fold(true.bind)(username => u.username like  username) &&
          birthdayStartOpt.fold(true.bind)(date => u.birthday >= date) &&
          birthdayEndOpt.fold(true.bind)(date => u.birthday <= date) &&
          creationTimeStartOpt.fold(true.bind)(date => u.creationTime >= date) &&
          creationTimeEndOpt.fold(true.bind)(date => u.creationTime <= date) &&
          updateTimeStartOpt.fold(true.bind)(date => u.updateTime >= date) &&
          updateTimeEndOpt.fold(true.bind)(date => u.updateTime <= date)
      ).map(_.map(UserDto.fromEntity))
    )

  override def findById(id: UUID): ZIOUserDTO =
    for {
      userOpt <- ZIO.fromFuture(_ => rep.findById(id))
      user <- ZIO.fromOption(userOpt)
        .mapError(_ => NotFoundException("Пользователь с указанным идентификатором не найден"))
    } yield UserDto.fromEntity(user)


  override def isUserExistByChatId(chatId: Long): Task[BooleanDTO] =
    ZIO.fromFuture(implicit ex => rep.exists(_.chatId === chatId).map(r => BooleanDTO(r)))

  override def isUserExistByUsername(username: String): Task[BooleanDTO] =
    ZIO.fromFuture(implicit ex => rep.exists(_.username === username).map(r => BooleanDTO(r)))

  override def getByChatId(chatId: Long): Task[UserDto] =
    for {
      userOpt <- ZIO.fromFuture(_ => rep.findOne(_.chatId === chatId))
      user <- ZIO.fromOption(userOpt)
        .mapError(_ => NotFoundException("Пользователь с указанным идентификатором телеграмма не найден"))
    } yield UserDto.fromEntity(user)

  override def getByUsername(username: String): ZIOUserDTO =
    for {
      userOpt <- ZIO.fromFuture(_ => rep.findOne(_.username === username))
      user <- ZIO.fromOption(userOpt)
        .mapError(_ => NotFoundException("Пользователь с указанным именем не найден"))
    } yield UserDto.fromEntity(user)
}