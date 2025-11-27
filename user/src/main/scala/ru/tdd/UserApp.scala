package ru.tdd

import org.flywaydb.core.Flyway
import ru.tdd.controller.configs.AppConfig
import ru.tdd.controller.routes.{AuthRoutes, UserRoutes}
import ru.tdd.database.entities.users.AppUserEntity
import slick.jdbc.PostgresProfile.api._
import zio.http.Server
import zio.{ZIO, ZIOAppDefault, ZLayer}

import scala.concurrent.Future

object UserApp extends ZIOAppDefault {

  override def run = {
    val config = AppConfig()
    val dbConfig = config.db
    val flywayConfig = config.flyway

    val db = Database.forURL(
      url = dbConfig.url,
      user = dbConfig.username,
      password = dbConfig.password,
      driver = dbConfig.driver
    )

    val flywayAction = Flyway
      .configure()
      .dataSource(
        dbConfig.url,
        dbConfig.username,
        dbConfig.password
      ).baselineVersion(flywayConfig.baseLineVersion)
      .baselineOnMigrate(flywayConfig.baseLineOnMigrate)
      .locations(flywayConfig.locations: _ *)
      .load()

    (for {
      _ <- ZIO.fromFuture(implicit ec => db.run(AppUserEntity.entities.schema.createIfNotExists))
      _ <- ZIO.fromFuture(implicit ec => Future {flywayAction.migrate()})
      _ <- Server.serve(AuthRoutes(db, config.jwt) ++ UserRoutes(db, config.jwt))
    } yield ())
      .provide(
        ZLayer.succeed(Server.Config.default.binding(config.server.url, config.server.port)),
        Server.live
      )
      .ensuring(ZIO.fromFuture(_ => db.shutdown).ignore)
  }
}