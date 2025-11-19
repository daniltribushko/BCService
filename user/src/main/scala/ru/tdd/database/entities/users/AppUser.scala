package ru.tdd.database.entities.users

import ru.tdd.app.models.enums.Role
import ru.tdd.app.models.enums.Role.mapperSeq
import ru.tdd.database.entities.{Dao, EntityVersion}
import slick.jdbc.PostgresProfile.api._
import slick.lifted.ProvenShape

import java.time.{LocalDate, LocalDateTime}
import java.util.UUID

/**
 * @author Tribushko Danil
 * @since 05.11.2025
 * Класс-сущность пользователя
 */
class AppUserEntity(tag: Tag) extends Table[AppUser](tag, "app_user") with EntityVersion {

  def username = column[String]("username", O.Unique)

  def chatId = column[Long]("chat_id", O.Unique)

  def roles = column[Seq[Role]]("roles")

  def birthday = column[LocalDate]("birthday")

  override def * : ProvenShape[AppUser] =
    (id, username, chatId, creationTime, updateTime, roles, birthday) <> (AppUser.tupled, AppUser.unapply)
}

object AppUserEntity {

  val entities = TableQuery[AppUserEntity]
}

/**
 * @author Tribushko Danil
 * @since 05.11.2025
 * Класс-оболочка пользователя
 */
case class AppUser(
                    id: UUID,
                    username: String,
                    chatId: Long,
                    creationTime: LocalDateTime,
                    updateTime: LocalDateTime,
                    roles: Seq[Role],
                    birthday: LocalDate
                  ) extends Dao
