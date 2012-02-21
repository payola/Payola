package cz.payola.data

import actors.Actor

trait IWebServiceManager extends Actor {
    def evaluateSparqlQuery(query: String): QueryResult;

    def getRelatedItems(id: String, relationType: String): QueryResult;
}