package ru.tdd.controller.routes

import ru.tdd.controller.configs.ServiceProxyConfig
import zio.ZIO
import zio.http.{Client, Header, Method, Path, Request, Response, Routes, Scheme, Status, URL, handler, string, trailing}

/**
 * @author Tribushko Danil
 * @since 06.11.2025
 *        Маршруты для переадресации запросов к микросервисам
 */
object GatewayRoute {

  def apply(services: Seq[ServiceProxyConfig]): Routes[Client, Nothing] = {

    Routes(
      Method.ANY / "api" / "v1" / trailing ->
        handler { (_: Path, req: Request) =>

          val segments = req.url.path.segments

          segments.drop(2).headOption match {
            case Some(serviceName) =>
              services.find(_.urlPatterns.contains(serviceName)) match {

                case Some(service) =>
                  val targetUrl =
                    req.url
                      .host(service.targetHost)
                      .port(service.targetPort)
                      .scheme(Scheme.HTTP)

                  Client.batched(
                    Request(
                      method = req.method,
                      url = targetUrl,
                      headers = req.headers.removeHeader(Header.Host.name),
                      body = req.body
                    )
                  ).catchAll { ex =>
                    ZIO.succeed(
                      Response.text(s"Gateway error: ${ex.getMessage}")
                        .status(Status.BadGateway)
                    )
                  }

                case None =>
                  ZIO.succeed(
                    Response.text(s"Service '$serviceName' not found")
                      .status(Status.NotFound)
                  )
              }

            case _ =>
              ZIO.succeed(Response.status(Status.BadRequest))
          }
        }
    )

  }
}
