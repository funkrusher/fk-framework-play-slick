import com.typesafe.config.ConfigFactory

name := "fk-framework-play-slick"

lazy val settings = Seq(
  organization := "org.fk",
  version := "1.0-SNAPSHOT",
  scalaVersion := "2.13.8",
)

lazy val dependencies = Seq(
  guice,
  "com.typesafe.play" %% "play-slick"            % "5.1.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "5.1.0",
  "org.mariadb.jdbc"   % "mariadb-java-client"   % "3.1.2",
  "org.scalaz"        %% "scalaz-core"           % "7.3.2",
  specs2               % Test,
)

// define projects

lazy val fk_codegen = (project in file("fk_codegen"))
  .settings(
    settings,
    libraryDependencies ++= dependencies ++ Seq(
      jdbc,
      "com.typesafe.slick" %% "slick-codegen"                  % "3.4.1",
      "com.dimafeng"       %% "testcontainers-scala-scalatest" % "0.39.3",
      "com.dimafeng"       %% "testcontainers-scala-mariadb"   % "0.39.3",
    ),
  )

lazy val fk_core = (project in file("fk_core"))
  .settings(
    settings,
    libraryDependencies ++= dependencies,
  )

lazy val fk_module_library = (project in file("fk_module_library"))
  .dependsOn(fk_core)
  .settings(
    settings,
    libraryDependencies ++= dependencies,
  )

lazy val fk_module_store = (project in file("fk_module_store"))
  .dependsOn(fk_core)
  .settings(
    settings,
    libraryDependencies ++= dependencies,
  )

lazy val fk_server = (project in file("fk_server"))
  .enablePlugins(PlayScala, SwaggerPlugin)
  .dependsOn(fk_core)
  .dependsOn(fk_module_library)
  .dependsOn(fk_module_store)
  .settings(
    settings,
    libraryDependencies ++= dependencies ++ Seq(
      "org.webjars" % "swagger-ui" % "4.11.1"
    ),
    PlayKeys.playDefaultPort := 9000,
    swaggerDomainNameSpaces := Seq("dtos"),
  )

lazy val root = project
  .in(file("."))
  .dependsOn(fk_server, fk_codegen)
  .aggregate(fk_server, fk_codegen)
  .settings(settings)

TaskKey[Unit]("codegen") := (Compile / runMain).toTask(" codegen.SlickCodegenApp").value

addCommandAlias("fk_codegen", ";project fk_codegen;compile;run")
addCommandAlias("fk_server", ";project fk_server;compile;run")
