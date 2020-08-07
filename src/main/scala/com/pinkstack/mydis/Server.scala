package com.pinkstack.mydis

import akka.NotUsed
import akka.actor._
import akka.stream.scaladsl._
import akka.util.ByteString
import com.pinkstack.mydis.Protocol.{Command, _}
import com.typesafe.scalalogging.LazyLogging

import scala.collection.mutable

object Server extends LazyLogging {
  private[this] var storage: mutable.Map[Key, Value] = mutable.LinkedHashMap[Key, Value]()

  val systemInteraction: PartialFunction[Either[CommandValidation, Command], Response] = {
    case Right(_: Ping) => Pong
    case Right(Echo(message)) => SimpleStringResponse(message)
    case Right(Info()) =>
      SimpleStringResponse {
        s"""# Server
           |java_version:${System.getProperty("java.version")}
           |java_specification_version:${System.getProperty("java.specification.version")}
           |scala_version:${util.Properties.versionNumberString}
           |redis_mode:standalone
           |""".stripMargin
      }
    case Right(Time()) =>
      ArrayIntReply(Array(42, 12))
    case Right(command: Command) =>
      val name = command.getClass.getSimpleName.toUpperCase
      Error(s"Implementation for $name is missing.")
    case Left(error: CommandValidation) =>
      logger.error(error.errorMessage)
      Error(error.errorMessage)
  }

  val storageInteraction: PartialFunction[Either[CommandValidation, Command], Response] = {
    case Right(Set(key, value)) =>
      storage += (key -> value)
      Ok

    case Right(Del(key)) =>
      storage.remove(key)
        .map(_ => IntegerReply(1))
        .getOrElse(IntegerReply(0))

    case Right(Get(key)) =>
      storage.get(key)
        .map(v => SimpleStringResponse(v))
        .getOrElse(Nil)

    case Right(Exist(key)) =>
      if (storage.exists { case (k: Key, _) => k == key }) IntegerReply(1)
      else IntegerReply(0)

    case Right(Strlen(key)) =>
      IntegerReply {
        storage.get(key)
          .map(_.length)
          .getOrElse(-1)
      }
  }

  val interactionFlow: Flow[String, String, NotUsed] =
    Flow[String]
      .map(Command.parse)
      .map(storageInteraction orElse systemInteraction)
      .collectType[Response]
      .map(_.message)

  val connectionFlow: Flow[ByteString, ByteString, NotUsed] =
    Flow[ByteString]
      .map(_.utf8String)
      .via(interactionFlow)
      .map(ByteString(_))

  def run(interface: String, port: Int): Unit = {
    implicit val system: ActorSystem = ActorSystem("mydis")
    import system.dispatcher

    val b = Tcp().bind(interface, port).runForeach { connection =>
      logger.info(s"New connection: ${connection.localAddress} <-> ${connection.remoteAddress}")
      connection.handleWith(connectionFlow)
    }

    b.onComplete(_ => system.terminate())
  }
}
