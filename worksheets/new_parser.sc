// https://medium.com/rahasak/scala-case-class-to-map-32c8ec6de28a

import scala.language.implicitConversions

sealed trait Command extends Product with Serializable

case class Set(key: String, value: String) extends Command

case class Get(key: String) extends Command

case class Mget(keys: List[String]) extends Command

object Mget {
  def apply(keys: String*): Mget = new Mget(keys.toList)
}

case class Mset(fields: List[(String, String)]) extends Command

object Mset {
  def apply(fields: (String, String)*): Mset = new Mset(fields.toList)

  def fromList(fields: List[String]) = {
    assert(fields.size % 2 == 0, "Mset needs odd number of fields.")
    new Mset(fields.sliding(2).map(x => (x.head, x.tail.head)).toList)
  }

  def apply(fields: List[String]): Mset = fromList(fields)

  def apply(fields: String*): Mset = fromList(fields.toList)
}

case object Time extends Command

case object Ping extends Command

case object Pong extends Command

object DSL {
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

    implicit private[this] def dslToString(dsl: MessageDsl): String = "DSL TO STRING"


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
      val commandName: String = command.getClass.getSimpleName

      val parametersValues: List[(String, Any)] =
        command.getClass.getDeclaredFields.map(_.getName)
          .zip(command.productIterator.toList)
          .toList

      val parameters: MessageDsl =
        parametersValues.flatMap {
          case (_, value: String) => List(($, value.length), (S, value))
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

import Protocol.Implicits._

println("---- set:")
println(Set("name", "Oto").message)

println("---- get:")
println(Get("name").message)

println("---- mget:")
println(Mget("key1", "key2"))

println("---- mset:")
println(Mset(("key1", "Hello"), ("key2", "World")))
println(Mset("key1", "Hello", "key2", "World"))

// println(Ping.message)
//println(Pong.message)
// Set("#", "x").productIterator