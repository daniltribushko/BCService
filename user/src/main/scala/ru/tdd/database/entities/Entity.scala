package ru.tdd.database.entities

import slick.jdbc.PostgresProfile.api._

import java.util.UUID

/**
 * @author Tribushko Danil
 * @since 05.11.2025
 *        Базовый класс-сущность
 */
trait Entity { self: Table[_] =>

  def id = column[UUID]("id", O.PrimaryKey)

}
