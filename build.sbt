import com.typesafe.config.ConfigFactory

import scala.collection.Seq

name := "fk-framework-play-slick"
lazy val settings = Seq(
  organization := "org.fk",
  version := "1.0-SNAPSHOT",
  scalaVersion := "2.13.12",
)

// versions
val playSlickVersion         = "5.1.0"
val mariadbJavaClientVersion = "3.1.2"
val scalazVersion            = "7.3.2"
val playScalaPdfVersion      = "4.3.0"
val slickVersion             = "3.4.1"
val testContainersVersion    = "0.39.3"
val scalaTestPlusPlay        = "5.1.0"
val scalaTest                = "3.2.17.0"
val jwtPlayJson              = "9.4.4"

// dependencies used in most projects
lazy val dependencies = Seq(
  guice,
  "com.typesafe.play"      %% "play-slick"            % playSlickVersion,
  "com.typesafe.play"      %% "play-slick-evolutions" % playSlickVersion,
  "org.mariadb.jdbc"        % "mariadb-java-client"   % mariadbJavaClientVersion,
  "org.scalaz"             %% "scalaz-core"           % scalazVersion,
  "com.hhandoko"           %% "play28-scala-pdf"      % playScalaPdfVersion,
  "com.github.jwt-scala"   %% "jwt-play-json"         % jwtPlayJson,
  "org.scalatestplus.play" %% "scalatestplus-play"    % scalaTestPlusPlay % "test",
  "org.scalatestplus"      %% "mockito-4-11"          % scalaTest         % "test",
)

// fk_codegen
lazy val fk_codegen = (project in file("fk_codegen"))
  .settings(
    settings,
    libraryDependencies ++= dependencies ++ Seq(
      jdbc,
      "com.typesafe.slick" %% "slick-codegen"                  % slickVersion,
      "com.dimafeng"       %% "testcontainers-scala-scalatest" % testContainersVersion,
      "com.dimafeng"       %% "testcontainers-scala-mariadb"   % testContainersVersion,
    ),
  )

// fk_core
lazy val fk_foundation = (project in file("fk_foundation"))
  .enablePlugins(PlayScala)
  .settings(
    settings,
    libraryDependencies ++= dependencies,
  )

// modules/fk_core
lazy val fk_core = (project in file("modules/fk_core"))
  .enablePlugins(PlayScala)
  .dependsOn(fk_foundation)
  .aggregate(fk_foundation)
  .settings(
    settings,
    libraryDependencies ++= dependencies,
  )

// modules/fk_library
lazy val fk_library = (project in file("modules/fk_library"))
  .enablePlugins(PlayScala)
  .dependsOn(fk_foundation)
  .aggregate(fk_foundation)
  .settings(
    settings,
    libraryDependencies ++= dependencies,
  )

// fk_server
lazy val fk_backend = (project in file("services/fk_backend"))
  .enablePlugins(PlayScala, UniversalPlugin, JavaAppPackaging, SwaggerPlugin)
  .dependsOn(fk_foundation, fk_core, fk_library)
  .settings(
    settings,
    Universal / javaOptions ++= Seq(
      "-Dconfig.file=application.conf",
      "-Dlogger.resource=logback.xml",
      "-J-Xms1536M",
      "-J-Xmx1536M",
      "-J-Xss1M",
      "-J-XX:+CMSClassUnloadingEnabled",
    ),
    libraryDependencies ++= dependencies,
    PlayKeys.playDefaultPort := 9000,
    swaggerV3 := true,
    swaggerTarget := new File("./services/fk_backend/conf/swagger-ui"),
    swaggerPrettyJson := true,
    swaggerDomainNameSpaces := Seq("library.dtos", "foundation.util"),
    // deploy-configuration (dist)
    logLevel := Level.Error,
    Universal / javaOptions ++= Seq(
      "-Dconfig.file=application.conf",
      "-Dlogger.resource=logback.xml",
      "-J-Xms1536M",
      "-J-Xmx1536M",
      "-J-Xss1M",
      "-J-XX:+CMSClassUnloadingEnabled",
    ),
    Universal / packageName := "dist",
    Universal / executableScriptName := "dist",
    Universal / topLevelDirectory := Some("dist"),
    Universal / mappings ++= com.typesafe.sbt.packager.MappingsHelper.directory("logs"),
    Compile / resourceDirectory := (Compile / resourceDirectory).value,
    Compile / packageSrc / publishArtifact := false,
    Compile / packageDoc / mappings := Seq(),
    Compile / packageDoc / publishArtifact := false,
    Universal / javaOptions ++= Seq(
      "-Dpidfile.path=/dev/null",
      "-agentlib:jdwp-transport=dt_socket,server=y,address=9010,suspend=n",
    ),
  )

// fk_scheduler
lazy val fk_scheduler = (project in file("services/fk_scheduler"))
  .enablePlugins(PlayScala, UniversalPlugin, JavaAppPackaging)
  .dependsOn(fk_foundation)
  .settings(
    settings,
    libraryDependencies ++= dependencies,
    PlayKeys.playDefaultPort := 9001,
    // deploy-configuration (dist)
    logLevel := Level.Error,
    Universal / javaOptions ++= Seq(
      "-Dconfig.file=application.conf",
      "-Dlogger.resource=logback.xml",
      "-J-Xms1536M",
      "-J-Xmx1536M",
      "-J-Xss1M",
      "-J-XX:+CMSClassUnloadingEnabled",
    ),
    Universal / packageName := "dist",
    Universal / executableScriptName := "dist",
    Universal / topLevelDirectory := Some("dist"),
    Universal / mappings ++= com.typesafe.sbt.packager.MappingsHelper.directory("logs"),
    Compile / resourceDirectory := (Compile / resourceDirectory).value,
    Compile / packageSrc / publishArtifact := false,
    Compile / packageDoc / mappings := Seq(),
    Compile / packageDoc / publishArtifact := false,
    Universal / javaOptions ++= Seq(
      "-Dpidfile.path=/dev/null",
      "-agentlib:jdwp-transport=dt_socket,server=y,address=9010,suspend=n",
    ),
  )

// root
lazy val root = project
  .in(file("."))
  .dependsOn(fk_backend, fk_scheduler, fk_codegen)
  .aggregate(fk_backend, fk_scheduler)
  .settings(settings, libraryDependencies ++= dependencies)

// tasks and commands
TaskKey[Unit]("codegen") := (Compile / runMain).in(fk_codegen).toTask(" codegen.SlickCodegenApp").value
addCommandAlias("fk_backend", ";project fk_backend;compile;run")
addCommandAlias("fk_scheduler", ";project fk_scheduler;compile;run")
