import shapeless._

sealed trait Command extends Product with Serializable

type Key = String
type Value = String

case class Set(key: Key, value: Value) extends Command

case class Get(key: Key) extends Command

case class Del(key: Key) extends Command

case class MGet(key: Key*) extends Command

case class MSet(values: (Key, Value)*) extends Command

// case object Ping extends Command

// case object Pong extends Command

// Generic[Set].to(Set("name", "Oto"))
// Generic[Get].from("name" :: HNil)

// type Commands = Set :+: Get :+: Del :+: MGet :+: MSet :+: Ping.type :+: CNil
// type Replies = Pong.type :+: CNil

// val set = Inl(Set)
// val del = Inl(Inl(Inl(Del)))

val gen = Generic[Command]
gen.to(Get("name"))

// With type classes
trait CommandEncoder[A] {
  def encode(value: A): List[String]
}

implicit val setEncoder: CommandEncoder[Set] = new CommandEncoder[Set] {
  override def encode(value: Set): List[String] = {
    List("oook")
  }
}

implicit val getEncoder: CommandEncoder[Get] = (value: Get) => {
  List("set me")
}

def encodeCommands[A](commands: List[A])(implicit encoder: CommandEncoder[A]): String = {
  commands.map(command => encoder.encode(command)).mkString("\n")
}

println(encodeCommands(List[Set](
  Set("name", "Oto"),
  Set("acc", "True"),
)))

def encodeCommand[A <: Command](command: A)(implicit e: CommandEncoder[A]): String = {
  val commandName = Option(command.getClass.getSimpleName).map { name =>
    Option.when(name.endsWith("$"))(name.substring(0, name.length - 1))
      .getOrElse(name)
  }.get

  val parametersValues: List[(String, Any)] =
    command.getClass
      .getDeclaredFields
      .map(_.getName)
      .zip(command.productIterator.toList)
      .toList

  val parameters: List[(String, Any)] =
    parametersValues.flatMap {
      case (_, value: String) => List(("$", value.length), ("", value))
      case (_, value: Int) => List(("$", value.toString.length), ("", value))
      case _ => throw new Exception("Unknown type")
    }

  (List(
    ("$", command.productArity + 1),
    ("$", commandName.length),
    ("", commandName)
  ) ++ parameters).map(_.productIterator.mkString("")).mkString("\r\n")
}

Set("name", "Oto").productElementNames.toList

encodeCommand(Set("name", "Oto"))
encodeCommand(Set("cnt", "100"))