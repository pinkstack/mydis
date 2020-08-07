// https://medium.com/rahasak/scala-case-class-to-map-32c8ec6de28a

import scala.language.implicitConversions

sealed trait Command extends Product with Serializable

case class Set(key: String, value: String) extends Command

case class Get(key: String) extends Command

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

  def message: String = messageDsl.foldLeft(S)((agg, c) =>
    agg + c.productIterator.mkString(S) + EOL)
}

object Protocol {

  object Implicits {

    import DSL._

    implicit private[this] val intToString: Int => ProtocolContent = _.toString

    /*
    implicit def setMessage(set: Set): Protocol = new Protocol {
      def messageDsl: MessageDsl = List(
        (*, 1),
        (S, "set")
      )
    }
    */
    implicit def getMessage(get: Get): Protocol = new Protocol {
      def messageDsl: MessageDsl = List(
        (*, 1),
        (S, "get")
      )
    }

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

    implicit def dslToString(dsl: MessageDsl): String = "DSL TO STRING"
  }

}

import Protocol.Implicits._

println("---- set:")
println(Set("name", "Oto").message)
println(Get("name").message)
println("---- time:")
println(Time.message)
// println(Ping.message)
//println(Pong.message)
// Set("#", "x").productIterator