package cz.payola.scala2json


class TestObjectClass(var str: String, i: Int) {
    var smth: String = "help"
}

object Tester {
    def main(args: Array[String]){
        val t: TestObjectClass = new TestObjectClass("Hello", 22)
        val s2json: JSONSerializer = new JSONSerializer(t)

        println(s2json.stringValue())
        
    }
}
