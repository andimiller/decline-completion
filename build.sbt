import xerial.sbt.Sonatype._

ThisBuild / version      := "0.0.1"
ThisBuild / scalaVersion := "2.13.11"

lazy val core = crossProject(JVMPlatform, NativePlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .in(file("."))
  .settings(
    organization           := "net.andimiller",
    name                   := "decline-completion",
    crossScalaVersions     := List("2.13.11", "3.3.0"),
    libraryDependencies ++= List(
      "com.monovore"  %%% "decline"           % "2.4.1",
      "org.typelevel" %%% "munit-cats-effect" % "2.0.0-M3" % Test
    ),
    publishTo              := sonatypePublishTo.value,
    licenses               := Seq("Apache 2.0" -> url("https://opensource.org/license/apache-2-0")),
    sonatypeProjectHosting := Some(GitHubHosting("andimiller", "decline-completion", "andi at andimiller dot net")),
    developers             := List(
      Developer(id = "andimiller", name = "Andi Miller", email = "andi@andimiller.net", url = url("http://andimiller.net"))
    )
  )
