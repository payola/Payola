package cz.payola.data

import scala.collection.mutable

class WebServicesManager extends IWebServiceManager {
    var webServices = mutable.Set[IPayolaWebService]();

    /**
     * Evaluates given SPARQL query.
     *
     * @param query - SPARQL query
     *
     * @return returns result in String.
     */
    def evaluateSparqlQuery(query: String): QueryResult = {
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
     *  Fills webServices member with available web services
     */
    def initWebServices() = {
        webServices += new FakeRdfWebService();
        webServices += new FakeTtlWebService();
    }
}
