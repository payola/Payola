package cz.payola.data

;

/**
 * The interface for web services to interact properly with payola framework.
 *
 * User: Ondra Heřmánek
 * Date: 15.12.11, 19:13
 */
trait IPayolaWebService {
    /**
     * Evaluate given Sparql query.
     *
     * @param query - Sparql query to evaluate
     *
     * @return returns query result in XML format
     */
    def evaluateSparqlQuery(query: String): String;
}