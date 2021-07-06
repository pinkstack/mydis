val stripLast: String => String = { s =>
  if (s.endsWith("$")) s.substring(0, s.length - 1) else s
}

val stripLast2: String => String = {
  s => Option.when(s.endsWith("$"))(s.substring(0, s.length - 1)).getOrElse(s)
}

stripLast("Oto$")
stripLast("Oto")

stripLast2("Oto$")
stripLast2("Oto")

val stripLast3: Object => String = {
  o =>
    Option(o.getClass.getSimpleName).map { name =>
      Option.when(name.endsWith("$"))(name.substring(0, name.length - 1))
        .getOrElse(name)
    }.get
}

class MyClass {

}

object MyObject

stripLast3("Oto$")
stripLast3("Oto")


stripLast3(MyObject)
stripLast3(new MyClass)