package ru.tdd.app.services.jwt

import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.{Claims, Jwts}
import ru.tdd.controller.configs.JwtConfig
import ru.tdd.database.entities.users.AppUser

import javax.crypto.SecretKey
import scala.jdk.CollectionConverters.MapHasAsJava

/**
 * @author Tribushko Danil
 * @since 05.11.2025
 *        Сервис для работы с jwt-токенами
 */
trait JwtService {

  /** Создание токена */
  def createToken(user: AppUser): String

  /** Получение данных из токена */
  def parseToken(token: String): Claims

  protected val secretKey: SecretKey
}

/**
 * @author Tribushko Danil
 * @since 05.11.2025
 */
class JwtServiceImp(conf: JwtConfig) extends JwtService {

  override def createToken(user: AppUser): String =
    Jwts.builder()
      .subject(user.username)
      .issuedAt(NOW)
      .expiration(NOW + Day)
      .claims(Map("roles" -> user.roles, "chatId" -> user.chatId).asJava)
      .signWith(secretKey)
      .compact()

  override def parseToken(token: String): Claims =
    Jwts.parser()
      .verifyWith(secretKey)
      .build()
      .parseSignedClaims(token)
      .getPayload

  protected val secretKey: SecretKey =
    Keys.hmacShaKeyFor(Decoders.BASE64.decode(conf.secret))
}