package cz.payola.data

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
        val message = mutable.ArrayBuffer[String]();
        message += "QUERY";
        message += query;

        // Asynchronously ask services for query result
        webServices.foreach(
            service =>
            {
                service ! message;
            }
        );

        Thread.sleep(3000);

        // Stop all actors
        //stop();
        println("Query executed.");
        return new QueryResult(queryResult.getRdf(), queryResult.getTtl());
        // */

        /*
        val rdfResult = new StringBuilder();
        val ttlResult = new StringBuilder();

        // Get result from every initialized web service
        // TODO: asynchronously?
        webServices.foreach(
            service =>
            {
                val response = service.evaluateSparqlQuery(query);

                // TODO: there must be a better way to do this
                // There is a different handling of ttl and rdf response
                if (response != null && response.size >= 0){
                    if (response.startsWith("<?xml"))
                        rdfResult.append(response);
                    else
                        ttlResult.append(response)
                }
            }
        );

        return new QueryResult(rdfResult.toString(), ttlResult.toString());
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
                case x : mutable.ArrayBuffer[String] =>
                    println ("Manager (AB): " + x.size);
                    if (x.size == 2) {
                        val action = x(0);
                        val parameter = x(1);

                        // Switch by action
                        action match {
                            case "RESULT" =>
                                // Save query result
                                if (parameter != null && parameter.size >= 0){
                                    if (parameter.startsWith("<?xml") || parameter.startsWith("<rdf"))
                                        queryResult.appendRdf(parameter);
                                    else
                                        queryResult.appendTtl(parameter);
                                }
                        }
                    }

                case msg =>
                    println("Manager (invalid):" + msg);

            }
        }
    }

    def stop() = {
        // Stop actor
        exit();

        // Stop all services actors
        //webServices.foreach(service => service.exit());
    }

    def logError(message:String) = {
        println(message);
    }
}
