import com.typesafe.config.ConfigFactory

name := "fk-framework-play-slick"

lazy val settings = Seq(
  organization := "org.fk",
  version := "1.0-SNAPSHOT",
  scalaVersion := "2.13.8",
)

val swagger = "1.6.1"

lazy val dependencies = Seq(
  guice,
  "com.typesafe.play" %% "play-slick"            % "5.1.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "5.1.0",
  "org.mariadb.jdbc"   % "mariadb-java-client"   % "3.1.2",
  "org.scalaz"        %% "scalaz-core"           % "7.3.2",
  "io.swagger"         % "swagger-annotations"   % swagger,
  "com.hhandoko"      %% "play28-scala-pdf"      % "4.3.0",
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
  .enablePlugins(PlayScala)
  .settings(
    settings,
    libraryDependencies ++= dependencies,
  )

lazy val fk_library = (project in file("modules/fk_library"))
  .enablePlugins(PlayScala)
  .dependsOn(fk_core)
  .aggregate(fk_core)
  .settings(
    settings,
    libraryDependencies ++= dependencies,
  )

lazy val fk_store = (project in file("modules/fk_store"))
  .enablePlugins(PlayScala)
  .dependsOn(fk_core)
  .aggregate(fk_core)
  .settings(
    settings,
    libraryDependencies ++= dependencies,
  )

lazy val fk_server = (project in file("fk_server"))
  .enablePlugins(PlayScala)
  .dependsOn(fk_core, fk_library, fk_store)
  .settings(
    settings,
    libraryDependencies ++= dependencies,
    PlayKeys.playDefaultPort := 9000,
  )

lazy val root = project
  .in(file("."))
  .dependsOn(fk_server, fk_codegen)
  .aggregate(fk_server)
  .settings(settings)

TaskKey[Unit]("codegen") := (Compile / runMain).in(fk_codegen).toTask(" codegen.SlickCodegenApp").value

addCommandAlias("fk_server", ";project fk_server;compile;run")

// Docker Stuff
Docker / maintainer := "bernd.services@pm.me"
Docker / packageName := "fk-framework-play-slick"
Docker / version := sys.env.getOrElse("BUILD_NUMBER", "0")
Docker / daemonUserUid := None
Docker / daemonUser := "daemon"
dockerExposedPorts := Seq(9000)
dockerBaseImage := "openjdk:8"
dockerRepository := sys.env.get("ecr_repo")
dockerUpdateLatest := true

// Tune Settings of Docker-Stuff
javaOptions in Universal ++= Seq(
  // JVM memory tuning
  "-J-Xmx2048m",
  "-J-Xms512m",
  // alternative, you can remove the PID file
  s"-Dpidfile.path=/dev/null",
  // Use separate configuration file for production environment
  s"-Dconfig.file=/opt/docker/conf/application.conf",
  // Use separate logger configuration file for production environment
  s"-Dlogger.file=/opt/docker/conf/logback.xml",
)
