package cz.payola.data.model.graph.test
/*
import cz.payola.data._
import cz.payola.scala2json.{JSONSerializerOptions, JSONSerializer}
import sparql.messages.ResultMessage
import model.graph.RDFGraph
import actors.Actor._
import actors.Actor

/**
  * This tester demonstrates RDF graph serialization to JSON.
  */
object RDF2ScalaTest extends Actor {
    val manager: WebServicesManager = new WebServicesManager(this);

    def main(args: Array[String]) {
        // Start this actor
        start();
        
        // Get services manager and init it
        manager.initialize()

        // The fake web services don't need any SPARQL query
        manager.evaluateSparqlQuery("")
    }

    def act() {
        react {
            case m : ResultMessage =>
                // Message contains id of query and its result
                // Create an RDF graph from the query result
                val rdfDoc = RDFGraph(m.result.rdf)

                // Serialize it.
                // WARNING: you *need* to use the JSONSerializerOptions.JSONSerializerOptionSkipObjectIDs
                // option, otherwise the JSONSerializer would add objectIDs automatically
                val serializer = new JSONSerializer(rdfDoc, JSONSerializerOptions.JSONSerializerOptionPrettyPrinting
                    | JSONSerializerOptions.JSONSerializerOptionIgnoreNullValues)
                println(serializer.stringValue)

                // Stop manager
                manager ! new StopMessage();

                // Stop itself
                exit();

            case msg =>
                println("Invalid message: " + msg);
        }
    }
}
*/

