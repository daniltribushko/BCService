package ru.tdd.database.repositories.users

import ru.tdd.database.entities.users.{AppUser, AppUserEntity}
import ru.tdd.database.repositories.CrudRepository
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

/**
 * @author Tribushko Danil
 * @since 05.11.2025
 *        Репозиторий для работы с пользователями
 */
abstract class UserRepository(db: Database) extends CrudRepository[AppUser, AppUserEntity](db) {

  def existsByUsernameAndChatId(username: String, chatId: Long): Future[Boolean]

}

class SlickUserRepository(db: Database) extends UserRepository(db) {

  override val entities: TableQuery[AppUserEntity] = AppUserEntity.entities

  override def existsByUsernameAndChatId(username: String, chatId: Long): Future[Boolean] =
    exists(u => u.username === username || u.chatId === chatId)
}
