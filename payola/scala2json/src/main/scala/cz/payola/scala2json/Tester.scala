package cz.payola.scala2json

import annotations._
import collection.mutable.{HashMap, ArrayBuffer}
import traits._

class TestObjectClass(var str: String, val i: Int) {
    var firstNull = null
    var arr: Array[String] = null
    var arr2: Array[String] = null
    var arrBuff: ArrayBuffer[Int] = null
    var hash: HashMap[String,  Int] = null
    var smth: String = "help"
    var obj: TestObjectClass = null
    val customized: FullyCustomizedClass = new FullyCustomizedClass("any string")
    val additionalFields = new AdditionalFieldsClass(333)
    val customFields = new CustomFieldsClass(222)

    @JSONFieldName(name = "heaven") var hell: Double = 33.0d
    @JSONTransient var transient: Int = 33
}

class FullyCustomizedClass(var str: String) extends JSONSerializationFullyCustomized {
    def JSONValue(options: Int) = {
        if ((options & JSONSerializerOptions.JSONSerializerOptionPrettyPrinting) != 0){
            "{\n\tid: 123\n}"
        }else{
            "{id:123}"
        }
    }
}

@JSONPoseableClass(otherClassName = "xxx.animalfarm.Horse") class AdditionalFieldsClass(val int: Int) extends JSONSerializationAdditionalFields {
    def additionalFieldsForJSONSerialization: scala.collection.mutable.Map[String, Any] = {
        val map = new scala.collection.mutable.HashMap[String, Any]()
        map.put("newField", 666)
        map
    }
}

@JSONUnnamedClass class CustomFieldsClass(val int: Int) extends  JSONSerializationCustomFields {
    def fieldNamesForJSONSerialization: scala.collection.mutable.Iterable[String] = {
        val keys = new ArrayBuffer[String]()
        keys += "field0"
        keys += "field1"

        keys
    }

    
    def fieldValueForKey(key: String): Any = {
        if (key == "field0")
            333
        else if (key == "field1")
            "Jell-O, Cocaine and Silicon-titted Marilyn Monroe"
        else
            null
    }
}

class User(val name: String){
    val groups = new ArrayBuffer[Group]()
}

class Group(val name: String){
    val users = new ArrayBuffer[User]()
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
        
        val s2json: JSONSerializer = new JSONSerializer(t, JSONSerializerOptionPrettyPrinting |
                                                            JSONSerializerOptionIgnoreNullValues)

        //println(s2json.stringValue)

        val u: User = new User("Franta")
        val g: Group = new Group("My group")
        u.groups += g
        g.users += u

        val cycleJSON: JSONSerializer = new JSONSerializer(u, JSONSerializerOptionPrettyPrinting |
            JSONSerializerOptionIgnoreNullValues)

        println(cycleJSON.stringValue)
        
    }
}
