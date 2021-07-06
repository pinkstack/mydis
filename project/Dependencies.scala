import sbt._

object Dependencies {
  type Version = String

  object V {
    val Akka: Version = "2.6.8"
    val Pureconfig: Version = "0.13.0"
    val Cats: Version = "2.0.0"
    val CatsEffect: Version = "2.1.4"
    val Decline: Version = "1.0.0"
    val LogbackClassic: Version = "1.2.3"
    val ScalaLogging: Version = "3.9.2"
    val AkkaSlf4j: Version = "2.6.8"
    val Atto: Version = "0.7.0"
    val ScalaTest: Version = "3.2.0"
    val Shapeless: Version = "2.3.3"
  }

  object D {
    lazy val configuration: Seq[ModuleID] = Seq(
      "com.github.pureconfig" %% "pureconfig" % V.Pureconfig
    )

    lazy val akka: Seq[ModuleID] = Seq(
      "com.typesafe.akka" %% "akka-actor" % V.Akka,
      "com.typesafe.akka" %% "akka-actor-typed" % V.Akka,
      "com.typesafe.akka" %% "akka-stream" % V.Akka,
      "com.typesafe.akka" %% "akka-slf4j" % V.AkkaSlf4j
    )

    lazy val fp: Seq[ModuleID] = Seq(
      "org.typelevel" %% "cats-core" % V.Cats,
      "org.typelevel" %% "cats-effect" % V.CatsEffect
    )

    lazy val shapeless: Seq[ModuleID] = Seq(
      "com.chuusai" %% "shapeless" % V.Shapeless,
    )

    lazy val cli: Seq[ModuleID] = Seq(
      "com.monovore" %% "decline" % V.Decline
    )

    lazy val logging: Seq[ModuleID] = Seq(
      "ch.qos.logback" % "logback-classic" % V.LogbackClassic,
      "com.typesafe.scala-logging" %% "scala-logging" % V.ScalaLogging
    )

    lazy val atto: Seq[ModuleID] = Seq(
      "org.tpolecat" %% "atto-core" % V.Atto,
      "org.tpolecat" %% "atto-refined" % V.Atto
    )

    lazy val test: Seq[ModuleID] = Seq(
      "org.scalatest" %% "scalatest" % V.ScalaTest % "test",
      "org.scalatest" %% "scalatest-flatspec" % V.ScalaTest % "test",
      "org.scalatest" %% "scalatest-shouldmatchers" % V.ScalaTest % "test"
    )
  }

}