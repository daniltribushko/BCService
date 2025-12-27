package ru.tdd.app.models.dto

import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

/**
 * @author Tribushko Danil
 * @since 20.12.2025
 */
case class BooleanDTO(result: Boolean)

object BooleanDTO {

  implicit val encoder: JsonEncoder[BooleanDTO] = DeriveJsonEncoder.gen[BooleanDTO]
  implicit val decoder: JsonDecoder[BooleanDTO] = DeriveJsonDecoder.gen[BooleanDTO]
}
