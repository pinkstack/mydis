import cats._
import cats.implicits._
import cats.data._

sealed trait MyError {
  def message: String
}

case class IFailedYou(name: String) extends MyError {
  def message = s"I failed ${name}"
}

object HorribleFail extends MyError {
  def message = "Horrible fail here"
}

object Validator {
  def validate(input: String): Validated[MyError, String] = {
    val validateName: String => Validated[MyError, String] = n =>
      Either.cond(n.contains("X"), n, IFailedYou(n)).toValidated

    val anotherValidityCheck: String => Validated[MyError, String] = n =>
      Either.cond(n.contains("god"), n, HorribleFail).toValidated

    for {
      validName <- validateName(input)
      validateAgain <- anotherValidityCheck(input)
    } yield validName
  }
}

Validator.validate("Oto")
Validator.validate("Mr X")
