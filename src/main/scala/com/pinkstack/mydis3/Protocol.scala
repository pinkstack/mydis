package com.pinkstack.mydis3

import cats._
import cats.implicits._
import cats.syntax.EitherSyntax._

import scala.util.{Failure, Try}

object Model {

  sealed trait Command

  case class Set(key: String, value: String) extends Command

  case class Get(key: String) extends Command

  case class Exists(key: String) extends Command

  case object Ping extends Command

  sealed trait Reply

  case class ArrayReply[T](array: Array[T]) extends Reply

  case class SimpleString(string: String) extends Reply

  case class Error(message: String) extends Reply

  case object OK extends Reply

  case object Pong extends Reply

}

object Protocol {

  import Model._

  private[Protocol] val `*` = "*"
  private[Protocol] val `+` = "+"
  private[Protocol] val `-` = "-"
  private[Protocol] val `:` = ":"
  private[Protocol] val `$` = "$"
  private[Protocol] val EOL = "\r\n"

  trait EncodingError {
    def errorMessage: String
  }

  final object GenericEncodingError extends EncodingError {
    def errorMessage: String = "Something went wrong with encoding."
  }

  trait Encoder[T <: Command] {
    def encode(command: T): List[String]
  }

  def encode(command: Command): Either[EncodingError, String] = {
    command.getClass.getSimpleName.asRight
  }

  def encode(reply: Reply): Either[EncodingError, String] = {
    reply.getClass.getSimpleName.asRight
  }

  /*
  implicit val commandSet = new Encoder[Set] {
    def encode(command: Set): List[String] = List("hello set")
  }

  implicit val replyEncoder = new Encoder[Reply] {
    override def encode(command: Reply): List[String] = List("command")
  }

  def encode[A <: Command](c: A)(implicit encoder: Encoder[A]): Either[EncodingError, String] = {
    encoder.encode(c).mkString(EOL).asRight
  }

  def encode[A <: Reply](c: A)(implicit encoder: Encoder[A]): Either[EncodingError, String] = {
    encoder.encode(c).mkString(EOL).asRight
  }

   */

}

object Parser {
  import shapeless._

  class ConverterException(s: String) extends RuntimeException(s)

  trait Converter[T] {
    def from(s: String): Try[T]
    def to(t: T): String
  }

  object Converter {
    def apply[T](implicit st: Lazy[Converter[T]]): Converter[T] = st.value

    def fail(s: String) = Failure(new ConverterException(s))
  }


  def parse(input: String) = {

  }
}