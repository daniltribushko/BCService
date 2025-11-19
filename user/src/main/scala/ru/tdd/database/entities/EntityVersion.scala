package ru.tdd.database.entities

import slick.jdbc.PostgresProfile.api._

import java.time.LocalDateTime

/**
 * @author Tribushko Danil
 * @since 05.11.2025
 *        Класс-сущность с временем создания и обновления
 */
trait EntityVersion extends Entity {
  self: Table[_] =>

  def creationTime = column[LocalDateTime]("creation_time")

  def updateTime = column[LocalDateTime]("update_time")
}
