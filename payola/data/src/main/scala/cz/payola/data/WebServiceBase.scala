package cz.payola.data

import collection.mutable
import cz.payola.data.messages._

abstract class WebServiceBase(manager : WebServicesManager) extends IPayolaWebService {
    require(manager != null, "Web service manager should be specified!");

    def evaluateSparqlQuery(query: String): String;

    def initialize() = {
        // Start web service actor
        start();
    }

    def act() = {
        loop {
            react {
                case x : QueryMessage =>
                    //println("Web service evaluates query with id:" + x.id);
                    
                    // Evaluate query and send result to the manager
                    manager ! new ResultMessageFromWebService(evaluateSparqlQuery(x.query), x.id);

                case x : StopMessage =>
                    exit();

                case msg =>
                    println("Service: (invalid)" + msg);
                    manager ! msg;
            }
        }
    }
}