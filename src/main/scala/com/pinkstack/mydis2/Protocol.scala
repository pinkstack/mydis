package com.pinkstack.mydis2

/*
  - https://medium.com/rahasak/scala-case-class-to-map-32c8ec6de28a
 */

import scala.language.implicitConversions

object DSL {
  type Key = String
  type Value = String

  type ProtocolSymbol = String
  type Prefix = ProtocolSymbol
  type Postfix = ProtocolSymbol
  val `$`: Prefix = "$"
  val `*`: Prefix = "*"
  val `:`: Prefix = ":"
  val `+`: Prefix = "+"
  val `-`: Prefix = "-"
  val `S`: Prefix = ""
  val EOL: Postfix = "\r\n"

  type ProtocolContent = String
  type MessageDsl = List[(ProtocolSymbol, ProtocolContent)]
}

import DSL._


sealed trait Command extends Product with Serializable

case class Set(key: Key, value: Value) extends Command

case class Get(key: Key) extends Command

sealed trait MultiCommand[T] {
  def fields: List[T]
}

case class Mget(fields: List[Key]) extends Command with MultiCommand[Key]

object Mget {
  def apply(fields: Key*): Mget = new Mget(fields.toList)
}

case class Mset(fields: List[(Key, Value)]) extends Command with MultiCommand[(Key, Value)]

object Mset {
  def apply(fields: String*): Mset = fromList(fields.toList)

  private[this] def fromList(fields: List[String]): Mset = {
    assert(fields.size % 2 == 0, "Mset needs odd number of fields.")
    new Mset(fields.sliding(2).map(x => (x.head, x.tail.head)).toList)
  }

  // def apply(fields: (Key, Value)*): Mset = new Mset(fields.toList)
  // def apply(fields: List[String]): Mset = fromList(fields)
}

case object Time extends Command

case object Ping extends Command

case object Pong extends Command


trait Protocol {

  import DSL._

  def messageDsl: MessageDsl

  def message: String =
    messageDsl.foldLeft(S)((agg, c) => agg + c.productIterator.mkString(S) + EOL)
}

object Protocol {

  object Implicits {

    import DSL._


    implicit private[this] val intToString: Int => ProtocolContent = _.toString

    // implicit private[this] def dslToString(dsl: MessageDsl): String = "DSL TO STRING"


    /*
    implicit def setMessage(set: Set): Protocol = new Protocol {
      def messageDsl: MessageDsl = List(
        (*, 1),
        (S, "set")
      )
    }

    implicit def getMessage(get: Get): Protocol = new Protocol {
      def messageDsl: MessageDsl = List(
        (*, 1),
        (S, "get")
      )
    }
     */

    implicit def genericMessage[A <: Command](command: A): Protocol = new Protocol {
      val commandName: String =
        Option(command.getClass.getSimpleName).map { name =>
          Option.when(name.endsWith("$"))(name.substring(0, name.length - 1))
            .getOrElse(name)
        }.get

      val parametersValues: List[(String, Any)] =
        command.getClass
          .getDeclaredFields
          .map(_.getName)
          .zip(command.productIterator.toList)
          .toList

      val parameters: MessageDsl =
        parametersValues.flatMap {
          case (_, value: String) => List(($, value.length), (S, value))
          case (_, value: Int) => List(($, value.toString.length), (S, value))
          case _ => List() //TODO: NUMBERS!
        }

      def messageDsl: MessageDsl =
        List[(ProtocolSymbol, ProtocolContent)](
          (*, parametersValues.size + 1),
          ($, commandName.length),
          (S, commandName)
        ) ++ parameters
    }
  }

}