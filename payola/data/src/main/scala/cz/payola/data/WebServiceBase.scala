package cz.payola.data

import collection.mutable
import cz.payola.data.messages._

abstract class WebServiceBase(manager : WebServicesManager) extends IPayolaWebService {

    def evaluateSparqlQuery(query: String): String;

    def initialize() = {
        // Start web service actor
        start();
    }

    def act() = {
        receive {
            case x : QueryMessage =>
                // Evaluate query and send result to the manager
                manager ! new ResultMessage(evaluateSparqlQuery(x.query));

            case msg =>
                println("Service: (invalid)" + msg);
                manager ! msg;
        }
    }
}