package cz.payola.scala2json

import annotations._
import collection.mutable.{HashMap, ArrayBuffer}

class TestObjectClass(var str: String, val i: Int) {
    var firstNull = null
    var arr: Array[String] = null
    var arr2: Array[String] = null
    var arrBuff: ArrayBuffer[Int] = null
    var hash: HashMap[String,  Int] = null
    var smth: String = "help"
    var obj: TestObjectClass = null

    @JSONFieldName(name = "heaven") var hell: Double = 33.0d
    @JSONTransient var transient: Int = 33
}

import JSONSerializerOptions._

object Tester {
    def main(args: Array[String]){
        val t: TestObjectClass = new TestObjectClass("Hello", 22)
        t.obj = new TestObjectClass("deeper", 4)
        t.arr = new Array[String](2)
        t.arr2 = t.arr
        t.arr(0) = "First"
        t.arr(1) = "Second"
        t.arrBuff = new ArrayBuffer[Int]()
        t.arrBuff += 3
        t.arrBuff += 4
        t.hash = new HashMap[String, Int]()
        t.hash.put("five", 5)
        t.hash.put("six", 6)
        
        val s2json: JSONSerializer = new JSONSerializer(t, JSONSerializerOptionPrettyPrinting | JSONSerializerOptionIgnoreNullValues)

        println(s2json.stringValue)
        
    }
}
