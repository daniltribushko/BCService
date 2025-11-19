package ru.tdd.app.models.exceptions

import zio.http.Status
import zio.http.Status.{BadRequest, Conflict, NotFound, UnprocessableEntity}

import java.time.LocalDateTime

/**
 * @author Tribushko Danil
 * @since 05.11.2025
 * @param statusCode http код ошибки
 * @param message    текст сообщения
 * @param timestamp  время ошибки
 *                   Общее исключение api приложения
 */
class ApiException(
                    val statusCode: Status,
                    val message: String,
                    val timestamp: LocalDateTime = NOW_TIME
                  ) extends RuntimeException(message)

/**
 * @author Tribushko Danil
 * @since 05.11.2025
 */
case class BadRequestException(
                                override val message: String,
                                override val timestamp: LocalDateTime = NOW_TIME
                              ) extends ApiException(BadRequest, message, timestamp)

/**
 * @author Tribushko Danil
 * @since 05.11.2025
 */
case class ValidationException(
                                override val message: String,
                                override val timestamp: LocalDateTime = NOW_TIME
                              ) extends ApiException(UnprocessableEntity, message, timestamp)

/**
 * @author Tribushko Danil
 * @since 05.11.2025
 */
case class NotFoundException(
                              override val message: String,
                              override val timestamp: LocalDateTime = NOW_TIME
                            ) extends ApiException(NotFound, message, timestamp)

/**
 * @author Tribushko Danil
 * @since 05.11.2025
 */
case class AlreadyExistException(
                                  override val message: String,
                                  override val timestamp: LocalDateTime = NOW_TIME
                                ) extends ApiException(Conflict, message, timestamp)
