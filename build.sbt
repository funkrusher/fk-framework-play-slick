import com.typesafe.config.ConfigFactory

name := "fk-framework-play-slick"

lazy val settings = Seq(
  organization := "org.fk",
  version := "1.0-SNAPSHOT",
  scalaVersion := "2.13.12",
)

// versions
val playSlickVersion          = "5.1.0"
val mariadbJavaClientVersion  = "3.1.2"
val scalazVersion             = "7.3.2"
val swaggerAnnotationsVersion = "1.6.1"
val playScalaPdfVersion       = "4.3.0"
val slickVersion              = "3.4.1"
val testContainersVersion     = "0.39.3"

// dependencies used in most projects
lazy val dependencies = Seq(
  guice,
  "com.typesafe.play" %% "play-slick"            % playSlickVersion,
  "com.typesafe.play" %% "play-slick-evolutions" % playSlickVersion,
  "org.mariadb.jdbc"   % "mariadb-java-client"   % mariadbJavaClientVersion,
  "org.scalaz"        %% "scalaz-core"           % scalazVersion,
  "io.swagger"         % "swagger-annotations"   % swaggerAnnotationsVersion,
  "com.hhandoko"      %% "play28-scala-pdf"      % playScalaPdfVersion,
  specs2               % Test,
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
lazy val fk_core = (project in file("fk_core"))
  .enablePlugins(PlayScala)
  .settings(
    settings,
    libraryDependencies ++= dependencies,
  )

// modules/fk_library
lazy val fk_library = (project in file("modules/fk_library"))
  .enablePlugins(PlayScala)
  .dependsOn(fk_core)
  .aggregate(fk_core)
  .settings(
    settings,
    libraryDependencies ++= dependencies,
  )

// modules/fk_store
lazy val fk_store = (project in file("modules/fk_store"))
  .enablePlugins(PlayScala)
  .dependsOn(fk_core)
  .aggregate(fk_core)
  .settings(
    settings,
    libraryDependencies ++= dependencies,
  )

// modules/fk_swagger_ui
lazy val fk_swagger_ui = (project in file("modules/fk_swagger_ui"))
  .enablePlugins(PlayScala)
  .settings(
    settings
  )
def addSwaggerUiIfEnabled(prj: Project): Project = {
  val includeSwaggerPlugin: Boolean = sys.props.getOrElse("includeSwaggerPlugin", "false") == "true"
  if (includeSwaggerPlugin) {
    prj.dependsOn(fk_swagger_ui)
  } else {
    prj
  }
}

// fk_server
lazy val fk_server = addSwaggerUiIfEnabled(project in file("fk_server"))
  .enablePlugins(PlayScala)
  .dependsOn(fk_core, fk_library, fk_store)
  .settings(
    settings,
    libraryDependencies ++= dependencies,
    PlayKeys.playDefaultPort := 9000,
  )

// fk_scheduler
lazy val fk_scheduler = addSwaggerUiIfEnabled(project in file("fk_scheduler"))
  .enablePlugins(PlayScala)
  .dependsOn(fk_core)
  .settings(
    settings,
    libraryDependencies ++= dependencies,
    PlayKeys.playDefaultPort := 9001,
  )

// root
lazy val root = project
  .in(file("."))
  .dependsOn(fk_server, fk_scheduler, fk_codegen)
  .aggregate(fk_server, fk_scheduler)
  .settings(settings)

// tasks and commands
TaskKey[Unit]("codegen") := (Compile / runMain).in(fk_codegen).toTask(" codegen.SlickCodegenApp").value
addCommandAlias("fk_server", ";project fk_server;compile;run")
addCommandAlias("fk_scheduler", ";project fk_scheduler;compile;run")

// Docker Stuff (TODO: is only sketched here, needs to be tuned)
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
