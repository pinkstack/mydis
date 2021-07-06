package com.pinkstack.mydis4

import java.net.InetAddress
import java.time.Instant

import cats._
import cats.implicits._
import shapeless._

import scala.util.{Failure, Success, Try}

// Domain
sealed trait Command extends Product with Serializable

case class Set(name: String, value: String) extends Command

case class Get(name: String) extends Command

case class Strlen(name: String) extends Command

case object Ping extends Command

// Parsing
trait Parser[T] {
  def apply(s: String): Either[String, T]
}

trait TryParser[T] extends Parser[T] {
  def parse(s: String): T

  def apply(s: String): Either[String, T] =
    Try(parse(s)).toEither.leftMap(_.getMessage)
}

object LogParsers {
  implicit val instantParser: TryParser[Instant] = (s: String) => Instant.parse(s)

  implicit val inetAddressParser: TryParser[InetAddress] = (s: String) => InetAddress.getByName(s)
}

trait LineParser[Out] {
  def apply(l: List[String]): Either[List[String], Out]
}

object LineParser {
  implicit val hnilParser: LineParser[HNil] = {
    case Nil => Right(HNil)
    case h +: _ => Left(List(s"Expected EOL got $h"))
  }

  implicit def hconsParser[H: Parser, T <: HList : LineParser]: LineParser[H :: T] = {
    case Nil => Left(List("Expected list element."))
    case h +: t =>
      val head = implicitly[Parser[H]].apply(h)
      val tail = implicitly[LineParser[T]].apply(t)
      (head, tail) match {
        case (Left(error), Left(errors)) => Left(error :: errors)
        case (Left(error), Right(_)) => Left(error :: Nil)
        case (Right(_), Left(errors)) => Left(errors)
        case (Right(h), Right(t)) => Right(h :: t)
      }
  }

  implicit def caseClassParser[Out, R <: HList](implicit gen: Generic[Out] {type Repr = R},
                                                reprParser: LineParser[R]): LineParser[Out] =
    (s: List[String]) => reprParser.apply(s).right.map(gen.from)

  def apply[A](s: List[String])(implicit parser: LineParser[A]): Either[List[String], A] =
    parser(s)
}

object ShapelessApp extends App {
  val examples: Map[String, String] = Map(
    "set" ->
      """*3
        |$3
        |set
        |$4
        |name
        |$3
        |Oto""".stripMargin,
    "get" ->
      """*2
        |$3
        |get
        |$4
        |name""".stripMargin,
    "strlen" ->
      """*2
        |$6
        |strlen
        |$4
        |name""".stripMargin,
    "ping" ->
      """*1
        |$4
        |ping""".stripMargin
  )

  examples.foreach { case (exampleName, input) =>
    println(s"\n--- Running example $exampleName ---\n")
    println(input)

    println {
      Semigroup[Int => Int].combine(_ + 1, _ * 10).apply(6)
    }
    println {
      Semigroup.combine(Map("foo" -> Map("bar" -> 5)), Map("foo" -> Map("bar" -> 6))).get("foo")
    }



  }
}
