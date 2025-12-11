package ru.tdd.database

import ru.tdd.database.entities.{Dao, Entity}
import slick.jdbc.PostgresProfile.api._

/**
 * @author Tribushko Danil
 * @since 05.11.2025
 *        Утилиты для работы с базой данных
 */
trait DbUtils {

  type EntityTable[T <: Dao] = Table[T] with Entity

}
