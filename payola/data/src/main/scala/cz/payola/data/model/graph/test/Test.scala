package cz.payola.data.model.graph.test

import cz.payola.data._
import cz.payola.scala2json.{JSONSerializerOptions, JSONSerializer}
import model.graph.RDFGraph

/*
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
*/

object RDF2ScalaTest {
    def main(args: Array[String]) {
        val manager: WebServicesManager = new WebServicesManager()
        manager.initWebServices()
        val queryResult: QueryResult = manager.evaluateSparqlQuery("")
        val rdfDoc = RDFGraph(queryResult.getRdf)
        val serializer = new JSONSerializer(rdfDoc, JSONSerializerOptions.JSONSerializerOptionPrettyPrinting
            | JSONSerializerOptions.JSONSerializerOptionIgnoreNullValues
            | JSONSerializerOptions.JSONSerializerOptionSkipObjectIDs)
        println(serializer.stringValue)

        // Old example demonstrating scardf library
        /*val john = UriRef( "http://doe.eg#john" )
        val g = Graph.build( john -(
            RDF.Type -> Person,
            isMale -> true,
            name -> "John Doe",
            height -> 167,
            likes -> ObjSet( Swimming, Science )
            ) )

        println(g.rend)

        val node: GraphNode = g/likes/asString
        println(node.toString())*/
    }
}

