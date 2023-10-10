addSbtPlugin("com.typesafe.play" % "sbt-plugin"           % "2.8.19")
addSbtPlugin("org.scalameta"     % "sbt-scalafmt"         % "2.4.6")
addSbtPlugin("net.vonbuchholtz"  % "sbt-dependency-check" % "4.3.0")

val includeSwaggerPlugin: Boolean = sys.props.getOrElse("includeSwaggerPlugin", "false") == "true"
if (includeSwaggerPlugin) {
  addSbtPlugin("com.github.dwickern" % "sbt-swagger-play" % "0.5.0")
} else {
  addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.4.6")
}
