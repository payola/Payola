package generated

import js.browser._

object Hello {
    def main() {
        val b = new B(4, "Ahoy")
        window.alert(b.x(3))
        window.alert(b.y("123*"))
        window.alert("Hello world")
        window.alert(window.location)
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