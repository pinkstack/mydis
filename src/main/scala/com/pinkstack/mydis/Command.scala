package com.pinkstack.mydis

import cats._
import cats.implicits._
import com.pinkstack.mydis.Protocol._

sealed trait CommandValidation {
  def errorMessage: String
}

case class UnknownCommand(name: String) extends CommandValidation {
  override def errorMessage: String = s"""Unknown or unimplemented command "$name"."""
}

case object UnknownParserFail extends CommandValidation {
  override def errorMessage: String = "Could not parse raw command."
}

object Command {
  val parse: String => Either[CommandValidation, Command] =
    _.split("\\r?\\n") match {
      case Array(numberOfParams, commandLength, commandName, rawParameters@_*)
        if numberOfParams.startsWith(*) && commandLength.startsWith($) =>

        val command = commandName.drop(0).toUpperCase
        val parameters: Array[String] = rawParameters.zipWithIndex.filter(_._2 % 2 != 0).map(_._1).toArray

        parameters match {
          case Array(key, value, _*) if command == "SET" =>
            Protocol.Set(key, value).asRight

          case Array(key) if command == "DEL" =>
            Protocol.Del(key).asRight

          case Array(key) if command == "GET" =>
            Protocol.Get(key).asRight

          case Array(key) if command == "EXIST" =>
            Protocol.Exist(key).asRight

          case Array(key) if command == "STRLEN" =>
            Protocol.Strlen(key).asRight

          case _ if command == "PING" =>
            Protocol.Ping().asRight

          case _ if command == "INFO" =>
            Protocol.Info().asRight

          case _ if command == "TIME" =>
            Protocol.Time().asRight

          case Array(commandName) if command == "ECHO" =>
            Protocol.Echo(commandName).asRight

          case _ =>
            UnknownCommand(command).asLeft
        }
      case _ =>
        UnknownParserFail.asLeft
    }

  private[this] val generateCommandTable: Command => List[TokenPair] = { command =>
    implicit val intToString: Int => String = _.toString
    val extractField: java.lang.reflect.Field => String = { field =>
      field.setAccessible(true)
      val r = field.get(command).toString
      field.setAccessible(false)
      r
    }
    val fields = command.getClass.getDeclaredFields

    List[TokenPair](
      (*, fields.length),
      ($, command.getClass.getSimpleName.length),
      (S, command.getClass.getSimpleName),
    ) ++ fields.map(extractField).flatMap { field =>
      List[TokenPair](
        ($, field.length),
        (S, field)
      )
    }
  }

  val generate: Command => String =
    generateCommandTable(_).map(_.productIterator.mkString).mkString("\r\n")
}