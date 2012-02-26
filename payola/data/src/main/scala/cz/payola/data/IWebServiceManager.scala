package cz.payola.data

import actors.Actor

trait IWebServiceManager extends Actor {
    def evaluateSparqlQuery(query: String): Int;

    def getRelatedItems(itemId: String, relationType: String): Int;
}