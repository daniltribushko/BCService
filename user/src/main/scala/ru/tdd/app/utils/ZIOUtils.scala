package ru.tdd.app.utils

import ru.tdd.app.models.dto.UserDto
import zio.Task

/**
 * @author Tribushko Danil
 * @since 20.11.2025
 *        Утилиты для работы с ZIO
 */
trait ZIOUtils {

  type ZIOUserDTO = Task[UserDto]

  type ZIOUserDTOS = Task[Seq[UserDto]]

  type ZUnit = Task[Unit]

}
