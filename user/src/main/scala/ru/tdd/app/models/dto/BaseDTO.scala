package ru.tdd.app.models.dto

import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder}

/**
 * @author Tribushko Danil
 * @since 20.11.2025
 *        Dto класс объекта с одним полем-названием
 */
case class BaseDTO(name: String)

object BaseDTO {

  implicit val encoder = DeriveJsonEncoder.gen[BaseDTO]
  implicit val decoder = DeriveJsonDecoder.gen[BaseDTO]
}