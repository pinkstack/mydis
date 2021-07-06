import Dependencies._
import Settings._
import sbt.Keys._
import sbt.nio.Keys._
import com.typesafe.sbt.packager.docker.Cmd
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport._

lazy val mydis = (project in file("."))
  .enablePlugins(JavaAppPackaging, DockerPlugin)
  .settings(sharedSettings: _*)
  .settings(
    name := "mydis",
    version := "0.2",
    libraryDependencies ++= Seq(
      D.fp,
      D.shapeless,
      D.atto,
      D.akka,
      D.configuration,
      D.cli,
      D.logging,
      D.test
    ).foldLeft(Seq.empty[ModuleID])(_ ++ _),
  )
  .settings(
    Compile / mainClass := Some("com.pinkstack.mydis.Main"),
    mainClass in assembly := Some("com.pinkstack.mydis.Main"),
    assemblyJarName in assembly := "mydis.jar"
  )
  .settings(
    dockerUsername := Some("pinkstack"),
    dockerBaseImage := "azul/zulu-openjdk-alpine:11-jre",
    maintainer in Docker := "Oto Brglez <otobrglez@gmail.com>",
    dockerAliases ++= Seq(dockerAlias.value.withTag(Option("latest"))),
    dockerExposedPorts ++= Seq(6667),
    dockerCommands := dockerCommands.value.flatMap {
      case add@Cmd("RUN", args@_*) if args.contains("id") =>
        List(
          Cmd("RUN", "apk add --no-cache bash"),
          Cmd("ENV", "SBT_VERSION", sbtVersion.value),
          Cmd("ENV", "SCALA_VERSION", scalaVersion.value),
          Cmd("ENV", "MYDIS_VERSION", version.value),
          add
        )
      case other => List(other)
    }
  )
