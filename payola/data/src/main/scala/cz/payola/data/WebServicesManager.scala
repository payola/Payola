package cz.payola.data

import messages._
import scala.collection.mutable

class WebServicesManager extends IWebServiceManager {
    var webServices = mutable.Set[IPayolaWebService]();

    var queryResult : QueryResult = new QueryResult("","");

    /**
     * Evaluates given SPARQL query.
     *
     * @param query - SPARQL query
     *
     * @return returns result in String.
     */
    def evaluateSparqlQuery(query: String): QueryResult = {
        ///*
        // Compose message
        val message = new QueryMessage(query);

        // Asynchronously ask services for query result
        webServices.foreach(
            service =>
            {
                service ! message;
            }
        );

        Thread.sleep(3000);

        // Will be modified
        println("Query executed.");
        return new QueryResult(queryResult.rdf, queryResult.ttl);
        // */
    }

    /**
     * Gets items related to given item by specified relation type.
     *
     * @param id - ID of item to search for related items
     * @param relationType - relation type of related items
     *
     * @return returns result in String
     */
    def getRelatedItems(id: String, relationType: String): QueryResult = {
        val result = new StringBuilder();

        // TODO:
        val query = id + relationType;

        return evaluateSparqlQuery(query);
    }

    /**
      *  Initializes web services
      */
    def initialize() = {
        // Start actor to process query result from webservices
        start();

        // Load available services list
        initWebServices();
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

    def act() = {
        loop {
            react {
                case x : QueryMessage =>
                    println ("Manager (QM): ");

                case x : ResultMessage =>
                    if (x.result.startsWith("<?xml") || x.result.startsWith("<rdf"))
                        queryResult.appendRdf(x.result);
                    else
                        queryResult.appendTtl(x.result);
            }
        }
    }

    def logError(message:String) = {
        println(message);
    }
}
