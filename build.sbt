Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val Versions = new {
  val zio            = "2.0.10"
  val zioInteropCats = "23.0.0.1"
  val smithy4s       = "0.17.5"
  val http4s         = "0.23.18"
}

val smithy4s = Seq(
  "com.disneystreaming.smithy4s" %% "smithy4s-core"   % Versions.smithy4s,
  "com.disneystreaming.smithy4s" %% "smithy4s-http4s" % Versions.smithy4s
)

val http4s = Seq(
  "org.http4s" %% "http4s-ember-server" % Versions.http4s
)

val zio = Seq(
  "dev.zio" %% "zio"              % Versions.zio,
  "dev.zio" %% "zio-interop-cats" % Versions.zioInteropCats
)

lazy val root = (project in file("."))
  .enablePlugins(Smithy4sCodegenPlugin)
  .settings(name := "smithy4s-auth-example")
  .settings(libraryDependencies ++= smithy4s ++ http4s ++ zio)
