package generated

import browser._

object Hello {
    def main() {
        val b = new B(4, "Ahoy")
        alert(b.x(3))
        alert(b.y("123*"))
        alert("Hello world")
        alert(window.location)
    }
}

class A(val foo: Int, bar: String) {
    def x(baz: Int): String = {
        bar.charAt(baz).toString;
    }
}

class B(foo: Int, bar: String) extends A(foo, bar) {
    def y(baz: String): String = {
        baz.charAt(foo).toString;
    }
}