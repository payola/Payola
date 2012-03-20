package cz.payola.domain

import cz.payola.scala2json.JSONSerializer
import entities.{Group, User}

object Test {
    def main(args: Array[String]){
        val u: User = new User("myUser")
        u.id
        
        val g: Group = new Group("myGroup", u)
        val member: User = new User("member")
        g.addMember(member)
        
        val serializer = new JSONSerializer()
        println(serializer.serialize(u))
        println(serializer.serialize(g))
    }
}
