package com.pinkstack.mydis

object Protocol {
  type Predicate = String
  type TokenPair = (Predicate, String)
  val `$`: Predicate = "$"
  val `*`: Predicate = "*"
  val `:`: Predicate = ":"
  val S: Predicate = ""
  val EOL: Predicate = "\r\n"

  type Key = String
  type Value = String

  sealed trait Command

  case class Set(key: Key, value: Value) extends Command

  case class Del(key: Key) extends Command

  case class Get(key: Key) extends Command

  case class Exist(key: Key) extends Command

  case class Strlen(key: Key) extends Command

  case class Ping() extends Command

  case class Info() extends Command

  case class Time() extends Command

  case class Echo(message: String) extends Command

  sealed trait Response {
    def message: String
  }

  case object NoResponse

  case object Ok extends Response {
    override def message: String = "+OK" + EOL
  }

  case object Nil extends Response {
    override def message: String = "$-1" + EOL
  }

  case class Error(rawMessage: String) extends Response {
    override def message: String = s"-ERR ${rawMessage}" + EOL
  }

  case object Pong extends Response {
    override def message: String = s"+PONG" + EOL
  }

  implicit val pairsToString: List[TokenPair] => String =
    _.map(_.productIterator.mkString)
      .mkString(EOL) + EOL

  case class SimpleStringResponse(string: String) extends Response {
    override def message: String =
      List[TokenPair](
        ($, string.length.toString),
        (S, string)
      )
  }

  case class IntegerReply(int: Int) extends Response {
    override def message: String =
      List[TokenPair]((`:`, int.toString))
  }

  case class ArrayStringReply(lines: Array[String]) extends Response {
    override def message: String = {
      List[TokenPair](
        (*, lines.length.toString)
      ) ++ lines.flatMap { line =>
        List[TokenPair](
          ($, line.length.toString),
          (S, line)
        )
      }
    }
  }

  case class ArrayIntReply(lines: Array[Int]) extends Response {
    override def message: String = {
      List[TokenPair](
        (*, lines.length.toString)
      ) ++ lines.flatMap { line =>
        List[TokenPair](
          (`:`, line.toString)
        )
      }
    }
  }
}
