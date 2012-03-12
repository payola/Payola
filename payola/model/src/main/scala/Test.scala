package cz.payola.model

import cz.payola.scala2json.{JSONSerializer, JSONSerializerOptions}

object Test {
    def main(args: Array[String]){
        val u: User = new User("myUser")
        u.id
        
        val g: Group = new Group("myGroup", u)
        val member: User = new User("member")
        g.addMember(member)
        
        val serializer = new JSONSerializer(u, JSONSerializerOptions.JSONSerializerOptionSkipObjectIDs | JSONSerializerOptions.JSONSerializerOptionPrettyPrinting)
        println(serializer.stringValue)

        val groupSerializer = new JSONSerializer(g, JSONSerializerOptions.JSONSerializerOptionSkipObjectIDs | JSONSerializerOptions.JSONSerializerOptionPrettyPrinting)
        println(groupSerializer.stringValue)
    }
}
