package cz.payola.scala2json

import annotations._
import collection.mutable.{HashMap, ArrayBuffer}
import traits._
import JSONSerializerOptions._

/**
  * This test shows some basic capabilities of the JSONSerializer
  */


// This is a basic class example. It includes objects of all the classes below,
// so that everything can be tested at once
class TestObjectClass(var str: String, val i: Int) {
    // Make sure null's are ignored when the ignore-null-values option is in effect
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

    // Testing field annotations
    @JSONFieldName(name = "heaven") var hell: Double = 33.0d
    @JSONTransient var transient: Int = 33
}

// This is an example of a fully customized class serialization
class FullyCustomizedClass(var str: String) extends JSONSerializationFullyCustomized {
    def JSONValue(ctx: Any, options: Int) = {
        if ((options & JSONSerializerOptions.JSONSerializerOptionPrettyPrinting) != 0){
            "{\n\tid: 123\n}"
        }else{
            "{id:123}"
        }
    }
}

// This is an example of a class that uses the poseable annotation
// which lets it use a different class's name
// Moreover this class demonstrates the additional fields trait
@JSONPoseableClass(otherClass = classOf[cz.payola.scala2json.JSONSerializationException]) class AdditionalFieldsClass(val int: Int) extends JSONSerializationAdditionalFields {
    def additionalFieldsForJSONSerialization(ctx: Any): scala.collection.mutable.Map[String, Any] = {
        val map = new scala.collection.mutable.HashMap[String, Any]()
        map.put("newField", 666)
        map
    }
}

// This example demonstrates custom fields trait as well as the unnamed-class annotation
@JSONUnnamedClass class CustomFieldsClass(val int: Int) extends  JSONSerializationCustomFields {
    def fieldNamesForJSONSerialization(ctx: Any): scala.collection.mutable.Iterable[String] = {
        val keys = new ArrayBuffer[String]()
        keys += "field0"
        keys += "field1"

        keys
    }

    
    def fieldValueForKey(ctx: Any, key: String): Any = {
        key match {
            case "field0" => 333
            case "field1" => "Jell-O, Cocaine and Silicon-titted Marilyn Monroe"
            case _ => null
        }
    }
}


// The user and group classes are used to demonstrate serialization
// of cyclic object references
class User(val name: String){
    val groups = new ArrayBuffer[Group]()
}
class Group(val name: String){
    val users = new ArrayBuffer[User]()
}





object Tester {
    def main(args: Array[String]){
        // Create the test object and serialize it
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


        val prettyOptions = JSONSerializerOptionPrettyPrinting | JSONSerializerOptionIgnoreNullValues | JSONSerializerOptionDisableCustomSerialization
        var serializer: JSONSerializer = new JSONSerializer(t, prettyOptions)
        println(serializer.stringValue)



        // Test cycles
        val u: User = new User("Franta")
        val g: Group = new Group("My group")
        u.groups += g
        g.users += u

        serializer = new JSONSerializer(u, prettyOptions)
        println(serializer.stringValue)

        // Test a simple value only
        serializer = new JSONSerializer(1, prettyOptions)
        println(serializer.stringValue)
        
    }
}
