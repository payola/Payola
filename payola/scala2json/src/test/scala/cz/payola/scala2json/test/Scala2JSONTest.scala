package cz.payola.scala2json.test

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import cz.payola.scala2json._
import cz.payola.scala2json.classes._
import cz.payola.scala2json.rules._
import collection.mutable.{HashMap, ArrayBuffer}
import scala.collection.mutable
import cz.payola.scala2json.rules.CustomValueSerializationRule
import cz.payola.scala2json.rules.BasicSerializationRule
import cz.payola.scala2json.rules.CustomSerializationRule
import cz.payola.scala2json.classes.SimpleSerializationClass

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

// GroupTrait is used for testing serialization of classes with fields of other classes/traits.
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

class Exception1(val message: String = "", val cause: Exception = null) extends RuntimeException with CommonException
class Exception2(override val message: String = "", cause: Exception = null) extends Exception1(message, cause)

trait CommonException {
    val message: String
}

class ExceptionSerializer extends JSONSerializer
{
    val rule = new CustomValueSerializationRule[Exception1]("message", { (_, exc) => exc.message })
    this.addSerializationRule(new SimpleSerializationClass(classOf[Exception1]), rule)
}

class Scala2JSONTest extends FlatSpec with ShouldMatchers {
    "JSONSerializer" should "handle cyclic dependencies and a BasicSerializationRule." in {
        val u: User = new User("Franta")
        val g: Group = new Group("My group")
        u.groups += g
        g.users += u

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

        serializer.outputFormat = OutputFormat.Condensed

        serializer.serialize(u) should equal ("""{"__class__":"cz.payola.scala2json.test.User","__objectID__":0,"name":"Franta","groups":{"__arrayClass__":"scala.collection.mutable.ArrayBuffer","__value__":[{"__class__":"cz.payola.scala2json.test.GroupTrait","__objectID__":2,"name":"My group","users":{"__arrayClass__":"scala.collection.mutable.ArrayBuffer","__value__":[{"__ref__":0}]}}]}}""")
        serializer.serialize(g) should equal ("""{"__class__":"cz.payola.scala2json.test.GroupTrait","__objectID__":0,"name":"My group","users":{"__arrayClass__":"scala.collection.mutable.ArrayBuffer","__value__":[{"__class__":"cz.payola.scala2json.test.User","__objectID__":2,"name":"Franta","groups":{"__arrayClass__":"scala.collection.mutable.ArrayBuffer","__value__":[{"__ref__":0}]}}]}}""")
    }

    "escapeString" should "escape string" in {
        JSONUtilities.escapeString("\"jame\"go\"\"to hess\"\"") should equal ("\"\\\"jame\\\"go\\\"\\\"to hess\\\"\\\"\"")
    }

    "exception" should "have a message field serialized" in {
        val serializer: ExceptionSerializer = new ExceptionSerializer()
        val exc = new IllegalArgumentException("Hello")

        val rule = new CustomValueSerializationRule[Exception2]("message", { (_, exc) => exc.message })
        serializer.addSerializationRule(new SimpleSerializationClass(classOf[Exception2]), rule)

        assume(serializer.serialize(exc).contains("Hello"))
    }

}
