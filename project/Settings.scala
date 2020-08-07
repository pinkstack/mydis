import sbt._
import sbt.Keys._
import sbt.nio.Keys._

object Settings {
  lazy val sharedSettings = Seq(
    scalaVersion := "2.13.3",

    scalacOptions ++= Seq(
      "-deprecation",
      "-encoding", "UTF-8",
      "-feature",
      "-explaintypes",
      "-unchecked",
      "-Xlint:-unused,_",
      "-language:implicitConversions",
      "-language:higherKinds",
      "-language:postfixOps",
      "-Yrangepos",
      "-target:jvm-1.11"
    ),
    resolvers ++= Seq(
      Resolver.bintrayRepo("ovotech", "maven"),
      "Confluent Maven Repository" at "https://packages.confluent.io/maven/",
      "jitpack" at "https://jitpack.io"
    )
  )
}
