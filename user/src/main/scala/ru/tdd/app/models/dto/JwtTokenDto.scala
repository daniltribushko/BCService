package ru.tdd.app.models.dto

import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder}

/**
 * @author Tribushko Danil
 * @since 05.11.2025
 * Класс-дто JWT токена
 */
case class JwtTokenDto(jwt: String)

object JwtTokenDto {

  implicit val jwtTokenEncoder = DeriveJsonEncoder.gen[JwtTokenDto]
  implicit val jwtTokenDeCoder = DeriveJsonDecoder.gen[JwtTokenDto]
}
