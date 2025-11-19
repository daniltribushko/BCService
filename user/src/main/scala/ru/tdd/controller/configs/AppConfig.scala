package ru.tdd.controller.configs

import com.typesafe.config.{Config, ConfigFactory}

import scala.jdk.CollectionConverters.ListHasAsScala

/**
 * @author Tribushko Danil
 * @since 05.11.2025
 * @param db     класс-конфигурация базы данных
 * @param flyway класс-конфигурация flyway
 * @param jwt    класс-конфигурация jwt токенов
 * @param server класс-конфигурация настроек сервера
 *               Конфигурация приложения
 */
case class AppConfig(
                      db: DbConfig,
                      flyway: FlywayConfig,
                      jwt: JwtConfig,
                      server: Server
                    )

object AppConfig {

  def apply(): AppConfig = {
    val config = ConfigFactory.load("app.conf").getConfig("user-app")
    AppConfig(
      DbConfig(config),
      FlywayConfig(config),
      JwtConfig(config),
      Server(config)
    )
  }
}

/**
 * @author Tribushko Danil
 * @since 05.11.2025
 * @param username имя владельца базы данных
 * @param password пароль базы данных
 * @param url      адрес подключения к базе данных
 * @param driver   драйвер для работы с базой данных
 *                 Конфигурация базы данных
 */
case class DbConfig(
                     username: String,
                     password: String,
                     url: String,
                     driver: String
                   )

object DbConfig {

  def apply(config: Config): DbConfig = {
    val dbConf = config.getConfig("db")
    DbConfig(
      dbConf.getString("username"),
      dbConf.getString("password"),
      dbConf.getString("url"),
      dbConf.getString("driver")
    )
  }
}

/**
 * @author Tribushko Danil
 * @since 05.11.2025
 * @param baseLineVersion базовая версия миграций
 * @param baseLineOnMigrate
 * @param locations пути к файлам-миграциям
 *                  Конфигурация flyway
 */
case class FlywayConfig(
                         baseLineVersion: String,
                         baseLineOnMigrate: Boolean,
                         locations: Seq[String]
                       )

object FlywayConfig {

  def apply(config: Config): FlywayConfig = {
    val flywayConf = config.getConfig("flyway")
    FlywayConfig(
      flywayConf.getString("baseLineVersion"),
      flywayConf.getBoolean("baseLineOnMigrate"),
      flywayConf.getStringList("locations").asScala.toSeq
    )
  }
}

/**
 * @author Tribushko Danil
 * @since 05.11.2025
 * @param secret подпись jwt токена
 *               Конфигурация jwt токенов
 */
case class JwtConfig(
                      secret: String
                    )

object JwtConfig {

  def apply(config: Config): JwtConfig = {
    val jwtConfig = config.getConfig("jwt")
    JwtConfig(
      jwtConfig.getString("secret")
    )
  }
}

/**
 * @author Tribushko Danil
 * @since 05.11.2025
 * @param port порт сервера
 * @param url адрес сервера
 *            Конфигурация сервера
 */
case class Server(
                   port: Int,
                   url: String
                 )

object Server {

  def apply(config: Config): Server = {
    val serverConfig = config.getConfig("server")
    new Server(
      serverConfig.getInt("port"),
      if (serverConfig.hasPathOrNull("url")) serverConfig.getString("url") else "localhost"
    )
  }
}