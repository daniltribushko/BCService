package ru.tdd.app.services.jwt

import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.{Claims, Jwts}
import ru.tdd.app.models.dto.JwtTokenDto
import ru.tdd.controller.configs.JwtConfig
import ru.tdd.database.entities.users.AppUser

import java.time.{LocalDateTime, ZoneId}
import javax.crypto.SecretKey
import scala.jdk.CollectionConverters.MapHasAsJava

/**
 * @author Tribushko Danil
 * @since 05.11.2025
 *        Сервис для работы с jwt-токенами
 */
trait JwtService {

  /** Создание токена */
  def createToken(user: AppUser): JwtTokenDto

  /** Получение данных из токена */
  def parseToken(token: String): Claims

  /** Валидация токена */
  def validateToken(token: String): Boolean

  protected val secretKey: SecretKey
}

/**
 * @author Tribushko Danil
 * @since 05.11.2025
 */
class JwtServiceImp(conf: JwtConfig) extends JwtService {

  override final def createToken(user: AppUser): JwtTokenDto = {
    val issuedAt = NOW
    val expiration = NOW + Day
    JwtTokenDto(
      Jwts.builder()
        .subject(user.username)
        .issuedAt(issuedAt)
        .expiration(expiration)
        .claims(Map("chatId" -> user.chatId, "id" -> user.id).asJava)
        .signWith(secretKey)
        .compact(),
      expiration.toInstant.atZone(ZoneId.systemDefault()).toLocalDateTime
    )
  }

  override final def parseToken(token: String): Claims =
    Jwts.parser()
      .verifyWith(secretKey)
      .build()
      .parseSignedClaims(token)
      .getPayload

  protected final val secretKey: SecretKey =
    Keys.hmacShaKeyFor(Decoders.BASE64.decode(conf.secret))

  override final def validateToken(token: String): Boolean = {
    val claims = parseToken(token)
    claims.getExpiration > NOW
  }
}