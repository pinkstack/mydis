package com.pinkstack.mydis

import org.scalatest.flatspec._
import org.scalatest.matchers.should.Matchers
import org.scalatest.{EitherValues, PartialFunctionValues}
import Protocol._

class CommandSpec extends AnyFlatSpec
  with EitherValues
  with PartialFunctionValues
  with Matchers {

  "parse" should "fail if broken protocol" in {
    Command.parse("") match {
      case Right(_) => fail("Should not pass")
      case Left(v) => assert(v == UnknownParserFail)
    }

    Command.parse("*d") match {
      case Right(_) => fail("Should not pass")
      case Left(v) => assert(v == UnknownParserFail)
    }

    Command.parse("*10") match {
      case Right(_) => fail("Should not pass")
      case Left(v) => assert(v == UnknownParserFail)
    }
  }

  it should "not work on unknown commands" in {
    Command.parse {
      """|*3
         |$4
         |dodo
         |$4
         |name
         |""".stripMargin
    } match {
      case Left(UnknownCommand(name: String)) => assert(name == "DODO")
      case _ => fail("needs to fail")
    }
  }

  it should "parse SET" in {
    Command.parse {
      """|*3
         |$3
         |set
         |$4
         |name
         |$3
         |Oto
         |""".stripMargin
    } match {
      case Right(command: Protocol.Set) =>
        assert(command.key == "name")
        assert(command.value == "Oto")
      case Left(value) =>
        fail(s"Parsing of command failed with ${value.errorMessage}")
      case _ =>
        fail("only SET")
    }
  }

  it should "parse GET" in {
    Command.parse {
      """|*3
         |$3
         |get
         |$4
         |name
         |""".stripMargin
    } match {
      case Right(command: Protocol.Get) =>
        assert(command.key == "name")
      case Left(value) =>
        fail(s"Parsing of command failed with ${value.errorMessage}")
      case _ =>
        fail("only GET")
    }
  }

  it should "parse ECHO" in {
    Command.parse(Command.generate(Protocol.Echo("Hello"))) match {
      case Right(command: Protocol.Echo) =>
        assert(command.message == "Hello")
      case _ =>
        fail("Should never happen")
    }
  }

  it should "parse PING" in {
    Command.parse(Command.generate(Protocol.Ping())) match {
      case Right(command: Protocol.Ping) =>
        assert(true)
      case _ =>
        fail("Should never happen")
    }
  }

  "generate" should "generate SET command" in {
    val rawCommand = Command.generate(Protocol.Set("name", "Oto"))
    Command.parse(rawCommand) match {
      case Right(command: Protocol.Set) =>
        assert(command.key == "name")
        assert(command.value == "Oto")
      case Left(value) =>
        fail(s"Failed with ${value.errorMessage}")
      case _ =>
        fail("should never happen")
    }
  }

  it should "generate GET command" in {
    val rawCommand = Command.generate(Protocol.Get("name"))
    Command.parse(rawCommand) match {
      case Right(command: Protocol.Get) =>
        assert(command.key == "name")
      case Left(value) =>
        fail(s"Failed with ${value.errorMessage}")
      case _ =>
        fail("should never happen")
    }
  }
}
