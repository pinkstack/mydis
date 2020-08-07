// https://medium.com/rahasak/scala-case-class-to-map-32c8ec6de28a
import scala.reflect.runtime.universe._

sealed trait Command

case class Get(key: String) extends Command

object Get

case class Set(key: String, value: String) extends Command

object Set

sealed trait Reply

object NoReply extends Reply

type String2Command = String => Command

object ParserOne {
  val commandsMap: Map[Type, String2Command] = Map(
    typeOf[Get] -> { s: String =>
      Get(s)
    },
    typeOf[Get] -> { s: String =>
      Set(s"key ${s}", s"value ${s}")
    }
  )

  def parse[T : TypeTag](input: String) = {
    print(commandsMap)
    println(typeOf[T])
    commandsMap.get(typeOf[T]).map(_.apply(input))
  }
}

ParserOne.parse[Get]("oto")
ParserOne.parse[Set]("oto")

ParserOne.commandsMap.keys

// commandsMap.get(Get.getClass).map(_.apply("Oto"))
// commandsMap.get(Set.getClass).map(_.apply("Oto"))

