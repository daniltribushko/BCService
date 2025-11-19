package ru.tdd

import ru.tdd.controller.configs.AppConfig
import ru.tdd.controller.routes.GatewayRoute
import zio.http.{Client, Server}
import zio.{ZIOAppDefault, ZLayer}

object GatewayApp extends ZIOAppDefault {

  override def run = {
    val config = AppConfig()

    (for {
      _ <- Server.serve(GatewayRoute(config.servicesConfig))
    } yield ())
      .provide(
        ZLayer.succeed(Server.Config.default.binding(config.server.url, config.server.port)),
        Server.live,
        Client.default
      )
  }
}