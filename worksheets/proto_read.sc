import cats._
import cats.implicits._
import shapeless._

import scala.util.{Try, Success}

sealed trait Command

type Key = String
type Value = String

case class Set(key: Key, value: Value) extends Command

case class Get(key: Key) extends Command

trait CommandParser[T] {
  def apply(s: String): Either[String, T]
}

trait TryParser[T] extends CommandParser[T] {
  def parse(s: String): T

  def apply(s: String): Either[String, T] = {
    Try(parse(s)).transform(
      s => Success(Right(s)),
      f => Success(Left(f.getMessage))
    ).get
  }
}