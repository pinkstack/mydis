package com.pinkstack.mydis4

import java.time.LocalDate
import java.util.{Calendar, Date, Locale}

import shapeless._, record._
import shapeless.PolyDefns.~>

case class Person(name: String, email: String, bornOn: Option[LocalDate] = None)

object ShapelessExerciseApp extends App {
  println("\n " * 1 + "🎈 " * 10 + getClass.getSimpleName + " 🎈" * 10 + "\n")

  val personGen = LabelledGeneric[Person]
  println(personGen)

  val me = Person("Oto", "otobrglez@gmail.com")
  val wife = Person("Martina", "martinahe@gmail.com", Some(LocalDate.now()))

  val rec = personGen.to(me)
  println(rec)
  println(rec.keys)
  println(rec.values)
  println(rec.get(Symbol("email")))

  println("---")

  println {
    personGen.from(rec.updateWith(Symbol("email"))(v => v + "x"))
  }

  println("---")

  type ISB = Int :+: String :+: Boolean :+: CNil
  val isb = Coproduct[ISB]("xxx")

  println {
    isb
  }
}
