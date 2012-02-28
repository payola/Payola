package cz.payola.data.model.graph.test

/*import actors.Actor
import cz.payola.data.WebServicesManager
import cz.payola.data.rdf.messages.ResultMessage
import cz.payola.data.messages.{StopMessage, ResultMessage}

object Main extends Actor
{
    val manager = new WebServicesManager(this);

    /**
      * Main method of data project
      */
    def main(args: Array[String]) = {
        // start actor
        start();

        // Init manager instance
        manager.initialize();

        // TODO: queryId contains query identifier - use it as you need
        val queryId = manager.evaluateSparqlQuery("select distinct ?Concept where {[] a ?Concept} LIMIT 100");
        val query2Id = manager.evaluateSparqlQuery("select distinct ?Concept where {[] a ?Concept} LIMIT 100");
    }

    def act() {
        var results = 0;
        loop {
            react {
                case m: ResultMessage =>
                    // Message contains id of query and its result
                    println("RDF (" + m.id + ") of size: " + m.result.rdf.size);
                    println("TTL (" + m.id + ") of size: " + m.result.ttl.size);

                    results += 1;

                    if (results == 2) {
                        // Stop manager
                        manager ! new StopMessage();

                        // Stop itself
                        exit();
                    }

                case msg =>
                    println("Main - (Invalid message): " + msg);
            }
        }
    }
}*/
