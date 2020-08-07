import scala.language.implicitConversions

sealed trait ArrayReplyMagnet {
  type Out

  def apply(): Out
}

object ArrayReplyMagnet {
  implicit def arrayOfStringsReply(strings: Array[String]): ArrayReplyMagnet {
    type Out = String
  } = new ArrayReplyMagnet {
    override type Out = String

    override def apply: Out = {
      s"GOT ${strings.length}"
    }
  }

  implicit def arrayOfIntsReply(xs: Array[Int]): ArrayReplyMagnet {
    type Out = String
  } = new ArrayReplyMagnet {
    override type Out = String

    override def apply: Out = {
      s"SUMED ${xs.sum}"
    }
  }
}

def render(magnet: ArrayReplyMagnet): magnet.Out = magnet()


render(Array(1, 2, 3))
render(Array("Oto", "Martina"))

// https://www.clianz.com/2016/04/26/scala-magnet-pattern/