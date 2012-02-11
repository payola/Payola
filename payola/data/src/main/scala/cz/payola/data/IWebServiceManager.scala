package cz.payola.data

trait IWebServiceManager {
    def evaluateSparqlQuery(query: String): QueryResult;

    def getRelatedItems(id: String, relationType: String): QueryResult;
}