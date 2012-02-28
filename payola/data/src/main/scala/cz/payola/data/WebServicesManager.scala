package cz.payola.data

import messages._
import rdf.messages.ResultMessage
import rdf.WebServiceManager
import scala.collection.mutable
import actors.Actor

class WebServicesManager(receiver : Actor) extends WebServiceManager {
    require(receiver != null, "Web service manager should have receiver!");

    /**
      * Max delay in seconds manager will wait for query result
      */
    // TODO: read from some ini file
    private val TIMEOUT_FOR_QUERY = 30;

    private var webServices = mutable.Set[IPayolaWebService]();

    private val queryResults = new mutable.HashMap[Int, QueryInfo]();

    /**
     * Evaluates given SPARQL query.
     *
     * @param query - SPARQL query
     *
     * @return returns internal id of query. Result message that is sent to receiver will have the same id.
     */
    def evaluateSparqlQuery(query: String): Int = {
        // Create key for this query
        val id = nextId();

        // Create and set timer to notify manager when query timeout passes (by sending messages)
        val timer = new Timer(this);
        timer.initialize();
        timer ! new TimerMessage(id, TIMEOUT_FOR_QUERY);

        // Create indexed item in hash map that contains query result, received results count amd timer pointer
        queryResults.put(id, new QueryInfo(new QueryResult(), 0, timer));

        // Asynchronously evaluate query
        println("Evaluating query '" + query + "' with id: " + id);
        webServices.foreach( service => service ! new QueryMessage(query, id) );

        // Return id of a query
        return id;
    }

    /**
     * Gets items related to given item by specified relation type.
     *
     * @param itemId - ID of item to search for related items
     * @param relationType - relation type of related items
     *
     * @return returns id of a query that searches for related items.
 *          Result message that is sent to receiver will have the same id.
     */
    def getRelatedItems(itemId: String, relationType: String): Int = {
        // TODO: build query properly
        val query = itemId + relationType;

        return evaluateSparqlQuery(query);
    }

    /**
      *  Initializes web services
      */
    def initialize() = {
        // Start actor to process query result from web services
        start();

        // Load available services list
        initWebServices();
    }

    def act() = {
        loop {
            react {
                case m : ResultMessageFromWebService =>
                    //println("Manager received result for query with id: " + m.id);

                    // Process properly query result from any web service
                    processResultMessage(m);

                case m : TimerMessage =>
                    //println("Manager received timeout message for query with id: " + m.id);

                    // Timeout passed, sent incomplete result
                    processTimerMessage(m);

                case _ : StopMessage =>
                    println("Manager is stopping ... ");

                    stop();

                case msg =>
                    logError("Manager (IM): " + msg);
            }
        }
    }

    def logError(message:String) = {
        println(message);
    }
    
    private def processResultMessage(m : ResultMessageFromWebService) = {
        // Get query info
        val info = queryResults.get(m.id).get;

        // Update query result
        if (m.result.startsWith("<?xml") || m.result.startsWith("<rdf"))
            info.result.appendRdf(m.result);
        else
            info.result.appendTtl(m.result);

        // Increase receiver results count
        info.count += 1;

        // All results are returned?
        if (info.count == webServices.size) {
            // Yes -> send result to receiver
            sendResult(m.id);
        }
        else {
            // There is still at least one awaiting query result -> save updated info (result count) and wait
            queryResults.update(m.id, info);
        }
    }
    
    private def processTimerMessage(m : TimerMessage) = {
        // Not all results came back from web services, return at least what has come
        if (queryResults.contains(m.id))
            sendResult(m.id);
    }
    
    private def sendResult(id : Int) = {
        // Send result to the registered receiver and stop its timer
        receiver ! new ResultMessage(queryResults.get(id).get.result, id);
        queryResults.get(id).get.timer ! new StopMessage();

        // Remove query info from hash map because query is completely processed
        queryResults.remove(id);
    } 

    private def nextId() : Int = {
        // TODO: not safe due queries results removing
        return queryResults.size + 1;
    }

    private def stop() = {
        // Stop services first
        webServices.foreach( service => service ! new StopMessage() );

        // Stop manager as Actor
        exit();
    }

    /**
      *  Fills webServices member with available web services
      */
    private def initWebServices() = {
        webServices += new FakeRdfWebService(this);
        webServices += new FakeTtlWebService(this);
        webServices += new VirtuosoWebService(this);

        // Start all services actors
        webServices.foreach(service => service.initialize());
    }
}
