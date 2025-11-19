package ru.tdd.app.utils

import java.time.{LocalDate, LocalDateTime}
import java.util.Date

/**
 * @author Tribushko Danil
 * @since 19.11.2025
 *        Утилиты для работы со временем
 */
trait DateUtils {

  val Second = 1000

  val Minute = Second * 60

  val Hour = Minute * 60

  val Day = Hour * 24

  val NOW = new Date()

  val NOW_TIME = LocalDateTime.now()

  val NOW_DATE = LocalDate.now()

  implicit class DateOps(date: Date) {

    def +(time: Long) = new Date(date.getTime + time)
  }
}
