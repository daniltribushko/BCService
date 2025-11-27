package ru.tdd.app.models.dto

import ru.tdd.database.entities.users.AppUser
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder}

import java.time.{LocalDate, LocalDateTime}
import java.util.UUID

/**
 * @author Tribushko Danil
 * @since 20.11.2025
 * Dto запроса для обновления пользователя
 */
case class UpdateUserDto(
                          chatId: Long,
                          username: String,
                          birthday: LocalDate
                        )

object UpdateUserDto {

  implicit val encoder = DeriveJsonEncoder.gen[UpdateUserDto]
  implicit val decoder = DeriveJsonDecoder.gen[UpdateUserDto]
}

/**
 * @author Tribushko Danil
 * @since 20.11.2025
 * Dto пользователя
 */
case class UserDto(
                  id: UUID,
                  chatId: Long,
                  username: String,
                  birthday: LocalDate,
                  creationTime: LocalDateTime,
                  updateTime: LocalDateTime,
                  roles: Seq[BaseDTO]
                  )

object UserDto {

  implicit val encoder = DeriveJsonEncoder.gen[UserDto]
  implicit val decoder = DeriveJsonDecoder.gen[UserDto]

  def fromEntity(user: AppUser): UserDto =
    UserDto(
      user.id,
      user.chatId,
      user.username,
      user.birthday,
      user.creationTime,
      user.updateTime,
      user.roles.map(r => BaseDTO(r.name))
    )
}