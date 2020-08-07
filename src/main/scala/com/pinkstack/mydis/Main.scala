package com.pinkstack.mydis

import cats._
import cats.implicits._
import com.monovore.decline._

object Main extends CommandApp(
  name = "mydis",
  header = "Remote dictionary server, an in-memory data structure with optional durability.",
  main = {
    val interfaceOpt = Opts.option[String]("interface", "Interface", short = "i")
      .withDefault("0.0.0.0")
    val portOpt = Opts.option[Int]("port", "Port", short = "p")
      .withDefault(6667)
    (interfaceOpt, portOpt).mapN(Server.run)
  }
)
