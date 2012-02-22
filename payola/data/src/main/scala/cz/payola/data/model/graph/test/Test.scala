package cz.payola.data.model.graph.test

import cz.payola.data._
import cz.payola.scala2json.{JSONSerializerOptions, JSONSerializer}
import model.graph.RDFGraph

/**
  * This tester demonstrates RDF graph serialization to JSON.
  */
object RDF2ScalaTest {

    def main(args: Array[String]) {
        // Get services manager and init it
        val manager: WebServicesManager = new WebServicesManager()
        manager.initialize()

        // The fake web services don't need any SPARQL query
        val queryResult: QueryResult = manager.evaluateSparqlQuery("")

        // Create an RDF graph from the query result
        val rdfDoc = RDFGraph(queryResult.getRdf)

        // Serialize it.
        // WARNING: you *need* to use the JSONSerializerOptions.JSONSerializerOptionSkipObjectIDs
        // option, otherwise the JSONSerializer would add objectIDs automatically
        val serializer = new JSONSerializer(rdfDoc, JSONSerializerOptions.JSONSerializerOptionPrettyPrinting
            | JSONSerializerOptions.JSONSerializerOptionIgnoreNullValues)
        println(serializer.stringValue)
    }

}

