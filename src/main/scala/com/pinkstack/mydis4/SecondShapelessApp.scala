package com.pinkstack.mydis4

import shapeless._, record._

object SecondShapelessApp extends App {
  println("\n " * 1 + "🎈 " * 10 + getClass.getSimpleName + " 🎈" * 10 + "\n")

  type Client = Long :: HNil
  type Admin = Long :: Boolean :: HNil
  type Role = Admin :+: Client :+: CNil
  type User = Long :: String :: Boolean :: Role :: HNil

  val admin: Role = Inl(2L :: true :: HNil)
  val me: User = 1 :: "Oto" :: true :: admin :: HNil

  println(me)
}
