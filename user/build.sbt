

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.17"

lazy val root = (project in file("."))
  .settings(
    name := "user",
    libraryDependencies ++=
      Seq(
        "dev.zio" %% "zio" % "2.1.22",
        "dev.zio" %% "zio-json" % "0.7.45",
        "dev.zio" %% "zio-http" % "3.5.1",
        "com.typesafe.slick" %% "slick" % "3.6.1",
        "com.typesafe" % "config" % "1.4.5",
        "io.jsonwebtoken" % "jjwt-api" % "0.13.0",
        "io.jsonwebtoken" % "jjwt-impl" % "0.13.0",
        "org.postgresql" % "postgresql" % "42.7.8",
        "org.flywaydb" % "flyway-core" % "9.22.3",
        "io.jsonwebtoken" % "jjwt-gson" % "0.13.0",
        "com.google.code.gson" % "gson" % "2.13.2"
      ),
    assembly / mainClass := Some("ru.tdd.UserApp"),
    assembly / assemblyJarName := "user_app.jar",
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
      case PathList("META-INF", "versions", "9", "module-info.class") => MergeStrategy.discard
      case PathList("META-INF", "versions", "11", "module-info.class") => MergeStrategy.discard
      case PathList("module-info.class") => MergeStrategy.discard
      case PathList("META-INF", "io.netty.versions.properties") => MergeStrategy.first
      case PathList("META-INF", "native-image", _) => MergeStrategy.first
      case PathList("META-INF", "services", _) => MergeStrategy.first
      case PathList("META-INF", _*) => MergeStrategy.discard
      case x =>
        val oldStrategy = (assembly / assemblyMergeStrategy).value
        oldStrategy(x)
    }
  )
