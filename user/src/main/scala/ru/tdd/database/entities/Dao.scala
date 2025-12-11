package ru.tdd.database.entities

import java.util.UUID

/**
 * @author Tribushko Danil
 * @since 06.11.2025
 *        Dao-класс для работы с базой данных
 */
trait Dao {

  def id: UUID
}
