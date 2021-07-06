package com.pinkstack.experimental.ex1

import com.pinkstack.experimental.ex1.ExampleApp.computeSize
import shapeless._


object ExampleApp extends App {
  val log: Any => Unit = Console.println
  val title: String => Unit = s => log("\n" + "🔥 " * 10 + s + " 🔥" * 10)

  log("\n" + "☢️ " * 10 + getClass.getSimpleName + " ☢️" * 10 + "\n")

  type Email = String

  case class Person(firstName: String, lastName: String, email: Email)

  val personGen = Generic[Person]

  val me = Person("Oto", "Brglez", "otobrglez@gmail.com")
  val she = Person("Martina", "Brglez", "martinahe@gmail.com")

  val meRepr: String :: String :: Email :: HNil = personGen.to(me)
  log(meRepr)

  val rudi: Person = personGen.from("Rudi" :: "Brglez" :: "none" :: HNil)

  val tupleGen = Generic[(String, String)]
  log(tupleGen.to(("status", "ok")))
  log(tupleGen.from("status" :: "fail" :: HNil))

  title("Generic coproducts")

  case class Red()

  case class Green()

  case class Blue()

  type Colors = Red :+: Green :+: Blue :+: CNil

  title("Switching encodings using Generics")

  sealed trait Shape

  final case class Rectangle(width: Double, height: Double) extends Shape

  final case class Circle(radius: Double) extends Shape

  val genShape = Generic[Shape]
  log(genShape.to(Circle(10.0)))
  log(genShape.to(Rectangle(10.0, 3.0)))

  type Shapes = Rectangle :+: Circle :+: CNil

  val rectangle = Coproduct[Shapes](Rectangle(10, 10))
  val circle = Coproduct[Shapes](Circle(33.0))

  object computeSize extends Poly1 {
    type Out = Double

    implicit def caseRectangle: Case[Rectangle] {
      type Result = Out
    } = at[Rectangle] { r => r.width * r.height }

    implicit def caseCircle: Case.Aux[Circle, Out] = at[Circle](c => 2 * Math.PI * c.radius)
  }

  log {
    rectangle.map(computeSize)
  }

  log(circle.map(computeSize))

}
