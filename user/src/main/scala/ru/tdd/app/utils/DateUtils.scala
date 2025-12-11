package ru.tdd.app.utils

import java.time.{LocalDate, LocalDateTime}
import java.util.Date

/**
 * @author Tribushko Danil
 * @since 19.11.2025
 *        Утилиты для работы со временем
 */
trait DateUtils {

  val Second: Int = 1000

  val Minute: Int = Second * 60

  val Hour: Int = Minute * 60

  val Day: Int = Hour * 24

  val NOW: Date = new Date()

  val NOW_TIME: LocalDateTime = LocalDateTime.now()

  val NOW_DATE: LocalDate = LocalDate.now()

  implicit class DateOps(date: Date) {

    def +(time: Long) = new Date(date.getTime + time)

    def <(value: Date): Boolean = date.before(value)

    def >(value: Date): Boolean = date.after(value)
  }
}
