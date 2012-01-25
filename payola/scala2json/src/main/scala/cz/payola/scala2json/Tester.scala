package cz.payola.scala2json

import annotations.JSONTransient
import annotation.target.field

class TestObjectClass(var str: String, i: Int) {
    var smth: String = "help"

    @JSONTransient @field var transient: Int = 33
}

object Tester {
    def main(args: Array[String]){
        val t: TestObjectClass = new TestObjectClass("Hello", 22)
        val s2json: JSONSerializer = new JSONSerializer(t)

        println(s2json.stringValue())
        
    }
}
