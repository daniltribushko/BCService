package ru.tdd.app.models.enums

import slick.jdbc.PostgresProfile.api._

/**
 * @author Tribushko Danil
 * @since 05.11.2025
 *        Класс роли пользователя
 */
sealed abstract class Role(val name: String)

object Role {

  implicit val mapperSeq = MappedColumnType.base[Seq[Role], String](
    _.map(_.name).mkString(","),
    _.split(",").map {
      case Admin.name => Admin
      case User.name => User
    }
  )

  /** Администратор приложения */
  case object Admin extends Role("admin")

  /** Обычный пользователь приложения */
  case object User extends Role("user")
}
