package cz.payola.data

import actors.Actor

/**
 * The interface for web services to interact properly with payola framework.
 *
 * User: Ondra Heřmánek
 * Date: 15.12.11, 19:13
 */
trait IPayolaWebService /*extends Actor*/ {
    /**
     * Evaluate given Sparql query.
     *
     * @param query - Sparql query to evaluate
     *
     * @return returns query result in XML format
     */
    def evaluateSparqlQuery(query: String): String;

    /**
      * Initialize connection or resources required for run
      */
    def initialize();

    /**
      * Actor method of web service.
      * Provides asynchronous query processing.
      */
    def act();
}