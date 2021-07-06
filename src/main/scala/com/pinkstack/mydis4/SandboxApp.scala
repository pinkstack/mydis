package com.pinkstack.mydis4

import cats._
import cats.implicits._

object SandboxApp extends App {
  println("\n " * 1 + "🎈 " * 10 + getClass.getSimpleName + " 🎈" * 10 + "\n")

  val meh: Map[String, String] = Semigroup.combine(Map("name" -> "Oto"), Map("email" -> "otobrglez@gmail.com"))
  println(meh)

  println {
    Monoid[Map[String, Int]].combineAll(List(Map("a" -> 1, "b" -> 2), Map("a" -> 3)))
  }

  println {
    List(1, 2, 3, 4, 5).foldMap(identity)
  }

  println {
    List(1, 2, 3, 4, 5).foldMap(i => (i, i.toString))
  }

  println {
    Functor[List].map(List("a", "b", "c", "d", "e"))(_.toUpperCase)
  }

  val upIt: Option[String] => Option[Int] = Functor[Option].lift(_.length)

  println {
    upIt("Oto".some)
  }

  // fproduct
  val s = List("Oto", "was", "here")
  val p = Functor[List].fproduct(s)(_.length).toMap
  println(p.get("Oto"))

  val listOpts = Functor[List] compose Functor[Option]
  println {
    listOpts.map(List(Some(1), None, Some(3)))(_ + 1)
  }

  val stringToInt: String => Int = Integer.parseInt

  // Cats - Apply
  println {
    Apply[Option].map(Some("99"))(stringToInt)
  }

  // Apply and compose
  val listOps = Apply[List] compose Apply[Option]
  val addOne: Int => Int = _ + 1

  println {
    listOps.ap(List(Some(addOne)))(List(Some(1), None, Some(3)))
  }

  // Apply / AP
  println {
    Apply[Option].ap(Some(addOne))(Some(12))
  }

  val addArity2 = (a: Int, b: Int) => a + b
  println(Apply[Option].ap2(Some(addArity2))(Some(1), None))

  println {
    Apply[Option].tuple2(Some(1), Some(2))
  }

  println {
    Apply[Option].map2(Some(1), Some(2))(addArity2)
  }

  println {
    (Option(1), Option(2))
  }

  println {
    (Option(1), Option(2)).mapN(addArity2)
  }

  val op2: (Option[Int], Option[Int]) = (1.some, 2.some)
  val op3 = (op2._1, op2._2, Option.empty[Int])
  println {
    op3.mapN((a, b, c) => a + b + c)
  }

  println {
    op3.apWith(Some((a: Int, b: Int, c: Int) => a + b + c))
  }

  println(op2.tupled)
  println(op3.tupled)

  println("\n# Applicative\n")

  println(Applicative[Option].pure(1))
  println(Applicative[List].pure(1))

  println {
    (Applicative[List] compose Applicative[Option]).pure(1)
  }

  /**
   * Applicative is a generalization of Monad, allowing expression of effectful computations in a pure functional way.
   *
   * Applicative is generally preferred to Monad when the structure of a computation is fixed a priori.
   * That makes it possible to perform certain kinds of static analysis on applicative value
   */
  println(Monad[Option].pure(1))
  println(Applicative[Option].pure(1))
}
