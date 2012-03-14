package cz.payola.scala2json

import annotations._
import collection.mutable.{HashMap, ArrayBuffer}

/**
  * This test shows some basic capabilities of the JSONSerializer
  */



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
        var serializer: JSONSerializer = new JSONSerializer()

        // Test cycles
        val u: User = new User("Franta")
        val g: Group = new Group("My group")
        u.groups += g
        g.users += u

        println(serializer.serialize(u))
        println(serializer.serialize(g))
        
    }
}
