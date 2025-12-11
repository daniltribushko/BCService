package ru.tdd.controller.routes

import ru.tdd.controller.configs.ServiceProxyConfig
import zio.ZIO
import zio.http.{Client, Header, Method, Request, Response, Routes, Status, URL, handler, string}

/**
 * @author Tribushko Danil
 * @since 06.11.2025
 *        Маршруты для переадресации запросов к микросервисам
 */
object GatewayRoute {

  def apply(services: Seq[ServiceProxyConfig]): Routes[Client, Nothing] = {
    Routes.fromIterable(
      services.map { service =>
        Method.ANY / service.urlPattern / string("anyPath") ->
          handler {
            (_: String, req: Request) =>
              val targetUrl = "http://" + s"${service.targetHost}:${service.targetPort}${req.url.path.encode}"
              Client.batched(
                Request(
                  method = req.method,
                  url = URL.decode(targetUrl).getOrElse{
                    throw new RuntimeException("неверный адрес")
                  },
                  headers = req.headers
                    .removeHeader(Header.Host.name)
                    .addHeader(Header.Host(service.targetHost, Some(service.targetPort))),
                  body = req.body
                )
              ).catchAll {
                case ex: RuntimeException => ZIO.succeed(Response.text(ex.getMessage).status(Status.BadRequest))
              }
          }
      }
    ) ++ Routes(
      Method.GET / "health-check" ->
        handler {
          _: Request =>
            ZIO.succeed(Response.text("Шлюз работает стабильно!").status(Status.Ok))
        }
    )
  }
}
