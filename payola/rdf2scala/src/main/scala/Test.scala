package cz.payola.rdf2scala.test

import org.scardf._
import org.joda.time.LocalDate
import org.joda._

object PeopleVocabulary extends Vocabulary( "http://person.eg#" ) {
  val Person = uriref( "Person" )
  val Hobby = uriref( "Hobby" )
  val Swimming = uriref( "Swimming" )
  val Science = uriref( "Science" )
  val likes = prop( "Likes" )
  val isMale = prop( "IsMale" )
  val height = propInt( "Height" )
    val name = propStr("Name")
}

import PeopleVocabulary._

object RDF2ScalaTest {
    def main(args: Array[String]){
        val john = UriRef( "http://doe.eg#john" )
        val g = Graph.build( john -(
            RDF.Type -> Person,
            isMale -> true,
            name -> "John Doe",
            height -> 167,
            likes -> ObjSet( Swimming, Science )
            ) )

        println(g.rend)

        val node: GraphNode = g/likes/asString
        println(node.toString())


    }
}

