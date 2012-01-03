package cz.payola.data;

/**
 * User: Ondra Heřmánek
 * Date: 15.12.11, 19:13
 */
trait IPayolaWebService {

    def evaluateSparqlQuery(query : String) : String;

    def getRelatedItems(id : String, relationType : String) : String;
}