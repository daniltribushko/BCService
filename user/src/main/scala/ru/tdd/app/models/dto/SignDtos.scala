package ru.tdd.app.models.dto

import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonEncoder}

import java.time.LocalDate

/**
 * @author Tribushko Danil
 * @since 05.11.2025
 * @param chatId идентификатор пользователя в телеграмм
 * @param username имя пользователя в телеграмм
 * @param birthday дата рождения пользователя
 *                 Класс-dto запроса на регистрацию пользователя
 */
case class SignUpDto(
                  chatId: Long,
                  username: String,
                  birthday: LocalDate
                  )

object SignUpDto {

  implicit val encoder = DeriveJsonEncoder.gen[SignUpDto]
  implicit val deCoder = DeriveJsonDecoder.gen[SignUpDto]
}

/**
 * @author Tribushko Danil
 * @since 05.11.2025
 * @param chatId идентификатор пользователя в телеграмм
 * @param username имя пользователя в телеграмм
 *                 Класс-dto запроса на авторизацию пользователя
 */
case class SignInDto(
                      chatId: Long,
                      username: String
                    )

object SignInDto {

  implicit val encoder = DeriveJsonEncoder.gen[SignInDto]
  implicit val deCoder = DeriveJsonDecoder.gen[SignInDto]
}