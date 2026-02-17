package ru.tdd.controller.configs

import com.typesafe.config.{Config, ConfigFactory}

import scala.jdk.CollectionConverters.ListHasAsScala

case class AppConfig(
                      server: ServerConfig,
                      servicesConfig: Seq[ServiceProxyConfig]
                    )

object AppConfig {

  def apply(): AppConfig = {
    val conf = ConfigFactory.load("app.conf").getConfig("gateway-app")
    new AppConfig(
      ServerConfig(conf),
      conf.getConfigList("services").asScala.map(ServiceProxyConfig(_)).toSeq
    )
  }
}

case class ServerConfig(
                         port: Int,
                         url: String
                       )

object ServerConfig {

  def apply(conf: Config): ServerConfig = {
    val serverConf = conf.getConfig("server")
    new ServerConfig(
      serverConf.getInt("port"),
      if (serverConf.hasPathOrNull("url")) serverConf.getString("url") else "localhost"
    )
  }
}

case class ServiceProxyConfig(
                               targetHost: String,
                               targetPort: Int,
                               urlPatterns: List[String]
                             )

object ServiceProxyConfig {

  def apply(conf: Config): ServiceProxyConfig =
    new ServiceProxyConfig(
      conf.getString("target-host"),
      conf.getInt("target-port"),
      conf.getStringList("url-patterns").asScala.toList
    )
}