package ru.tdd.app.models.dto

import ru.tdd.app.models.exceptions.ApiException
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

import java.time.LocalDateTime

/**
 * @author Tribushko Danil
 * @since 05.11.2025
 * @param message   текст ошибки
 * @param timestamp время ошибки
 *                  Dto-объект исключения
 */
case class ExceptionDto(
                         message: String,
                         timestamp: LocalDateTime
                       )

object ExceptionDto {

  implicit val encoder: JsonEncoder[ExceptionDto] = DeriveJsonEncoder.gen[ExceptionDto]
  implicit val decoder: JsonDecoder[ExceptionDto] = DeriveJsonDecoder.gen[ExceptionDto]

  def apply(ex: ApiException): ExceptionDto =
    ExceptionDto(ex.getMessage, ex.timestamp)
}