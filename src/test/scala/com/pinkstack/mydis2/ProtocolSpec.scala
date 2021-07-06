package com.pinkstack.mydis2

import org.scalatest.flatspec._
import org.scalatest.matchers.should.Matchers
import org.scalatest.{EitherValues, PartialFunctionValues}

import Protocol._
import Protocol.Implicits._

class ProtocolSpec extends AnyFlatSpec
  with EitherValues
  with PartialFunctionValues
  with Matchers {

  "set" should "work" in {
    val s = Set("name", "Oto").message
    println(s)
  }

  it should "mset" in {
    val m2 = Mset("key1", "Hello", "key2", "World")
    println("---")
    println(m2)
    println("---")
  }

  it should "get" in {
    val m = Get("name").message
    println(m)
  }

  it should "ping" in {
    val m = Ping.message
    println(m)
  }

  it should "pong" in {
    val m = Pong.message
    println(m)
  }
}
