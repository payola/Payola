package cz.payola.scala2json

import classes.SimpleSerializationClass
import collection.mutable.{HashMap, ArrayBuffer}
import rules.BasicSerializationRule
import java.lang.reflect.Field

/**
  * This test shows some basic capabilities of the JSONSerializer
  */

// The user and group classes are used to demonstrate serialization
// of cyclic object references
class User(val _name: String)
{
    val groups = new ArrayBuffer[Group]()

    val _ignoreMe = true
}

trait GroupTrait
{
    val name: String = ""

    val users: Seq[User]

    def someMethod = {
    }
}

class Group(val name: String)
{
    val users = new ArrayBuffer[User]()

    val thisIsNotInTrait = null
}

object Tester
{
    def main(args: Array[String]) {
        // Test cycles
        val u: User = new User("Franta")
        val g: Group = new Group("My group")
        u.groups += g
        g.users += u

        val originalClass: Class[_] = classOf[GroupTrait]
        val originalFields: Array[Field] = originalClass.getDeclaredFields
        println(originalFields.length)
        originalFields foreach {f: Field =>
            println(f.getName)
        }

        println(originalClass)


        // Create the test object and serialize it
        val serializer: JSONSerializer = new JSONSerializer()

        // User rule:
        val userClass = new SimpleSerializationClass(classOf[User])
        val userMap = new HashMap[String, String]()
        userMap.put("_name", "name")
        val userRule = new BasicSerializationRule(None, Some(List("_ignoreMe")), Some(userMap))
        serializer.addSerializationRule(userClass, userRule)

        // Group rule:
        val groupClass = new SimpleSerializationClass(classOf[Group])
        val groupRule = new BasicSerializationRule(Some(classOf[GroupTrait]), None, None)
        serializer.addSerializationRule(groupClass, groupRule)

        serializer.outputFormat = OutputFormat.PrettyPrinted

        println(serializer.serialize(u))
        println(serializer.serialize(g))
    }
}
