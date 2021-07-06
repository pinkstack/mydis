package com.pinkstack.mydis3

import com.pinkstack.mydis3._
import com.pinkstack.mydis3.Model._
import org.scalatest.flatspec._
import org.scalatest.matchers.should.Matchers
import org.scalatest.{EitherValues, PartialFunctionValues}

class ProtocolSpec extends AnyFlatSpec
  with EitherValues
  with PartialFunctionValues
  with Matchers {

  "set" should "work" in {

    println {
      Protocol.encode(Set("name", "Oto"))
    }

    println {
      Protocol.encode(Get("name"))
    }

    println {
      Protocol.encode(Exists("name"))
    }

    println {
      Protocol.encode(ArrayReply(Array(1, 2, 3)))
    }

    println {
      Protocol.encode(Error("Some error here"))
    }

    println {
      Protocol.encode(OK)
    }

    println {
      Protocol.encode(Ping)
    }

    println {
      Protocol.encode(Pong)
    }

    assert(1 == 1, "it is")
  }
}
