import cats._
import cats.implicits._

sealed trait Command

type Key = String
type Value = String

case class Set(key: Key, value: Value)

case class Get(key: Key)

def parseGet(input: String): Option[Get] = Get(s"go, ${input}").some

def parseSet(input: String): Option[Set] = if (input.contains("oto")) None else Set("x", input).some

val p: String => Option[String] = (parseGet _, parseSet _).mapN {
  case (Some(get), None) =>
    s"yey ${get}".some
  case _ => None
}

p("oto")
p("martina")