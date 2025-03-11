import sbt.*

object Dependencies {

  object Version {
    val scala        = "2.13.10"
    val zio          = "2.1.15"
    val zioHttp      = "3.0.1"
    val zioConfig    = "4.0.3"
    val sl4j         = "2.0.16"
    val zioLogging   = "2.3.1"
    val logback      = "1.5.8"
    val scalaLogging = "3.9.5"
    val circe        = "0.14.10"
    val catsEffect   = "3.4.8"
    val spark        = "3.5.5"
    val quill        = "4.8.3"
    val postgre      = "42.7.3"
  }

  object ZIO {
    lazy val core    = "dev.zio" %% "zio"         % Version.zio
    lazy val macros  = "dev.zio" %% "zio-macros"  % Version.zio
    lazy val streams = "dev.zio" %% "zio-streams" % Version.zio
  }

  object CIRCE {
    lazy val core    = "io.circe" %% "circe-core"        % Version.circe
    lazy val generic = "io.circe" %% "circe-generic"     % Version.circe
    lazy val parse   = "io.circe" %% "circe-parser"      % Version.circe
  }

  object LOGS {
    lazy val sl4j           = "org.slf4j"                   % "slf4j-api"          % Version.sl4j
    lazy val logback        = "ch.qos.logback"              % "logback-classic"    % Version.logback
    lazy val zioLogging     = "dev.zio"                    %% "zio-logging"        % Version.zioLogging
    lazy val zioLoggingLf4j = "dev.zio"                    %% "zio-logging-slf4j2" % Version.zioLogging
    lazy val scalaLogging   = "com.typesafe.scala-logging" %% "scala-logging"      % Version.scalaLogging
  }

  object HTTP {
    lazy val zhttp = "dev.zio" %% "zio-http" % Version.zioHttp
  }

  object CATS {
    lazy val catsEffect = "org.typelevel" %% "cats-effect" % Version.catsEffect
  }

  object SPARK {
    lazy val core = "org.apache.spark" %% "spark-core" % Version.spark
    lazy val sql  = "org.apache.spark" %% "spark-sql"  % Version.spark
  }

  object CONFIG {
    lazy val core     = "dev.zio" %% "zio-config"          % Version.zioConfig
    lazy val magnolia = "dev.zio" %% "zio-config-magnolia" % Version.zioConfig
    lazy val typesafe = "dev.zio" %% "zio-config-typesafe" % Version.zioConfig
    lazy val refined  = "dev.zio" %% "zio-config-refined"  % Version.zioConfig
  }

  object STORAGE {
    lazy val quill   = "io.getquill"   %% "quill-jdbc-zio" % Version.quill
    lazy val postgre = "org.postgresql" % "postgresql"     % Version.postgre
  }

  lazy val globalProjectDependencies = Seq(
    ZIO.core,
    ZIO.macros,
    ZIO.streams,
    CIRCE.core,
    CIRCE.generic,
    CIRCE.parse,
    LOGS.scalaLogging,
    LOGS.logback,
    LOGS.zioLoggingLf4j,
    LOGS.zioLogging,
    LOGS.sl4j,
    HTTP.zhttp,
    CATS.catsEffect,
    SPARK.core,
    SPARK.sql,
    CONFIG.typesafe,
    CONFIG.refined,
    CONFIG.magnolia,
    CONFIG.core,
    STORAGE.quill,
    STORAGE.postgre
  )
}
