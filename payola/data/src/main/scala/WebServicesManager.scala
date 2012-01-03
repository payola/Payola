package cz.payola.data

import scala.collection.mutable

/**
 * Manages communication with web services - with all payola web services and local web service.
 *
 * User: Ondřej Heřmánek
 * Date: 15.12.11, 19:21
 */
class WebServicesManager {
    var webServices = mutable.Set[IPayolaWebService]();

    /**
     * Evaluates given SPARQL query.
     *
     * @param query - SPARQL query
     *
     * @return returns result in String.
     */
    def evaluateSparqlQuery(query: String): QueryResult = {
        val result = new StringBuilder();

        // Get result from every initialized web service
        for (val service <- this.webServices) {
            result.append(service.evaluateSparqlQuery(query));
        }

        return new QueryResult(result.toString());
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

        for (val service <- this.webServices) {
            result.append(service.evaluateSparqlQuery(query));
        }

        return new QueryResult(result.toString());
    }

    /**
     *  Fills webServices member with available web services
     */
    def initWebServices() = {
        this.webServices += new FakeWebService();
    }
}
