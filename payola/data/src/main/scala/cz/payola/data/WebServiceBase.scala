package cz.payola.data

import collection.mutable

/**
  * User: Ondřej Heřmánek
  * Date: 22.2.12, 15:52
  */

class WebServiceBase(manager : WebServicesManager) extends IPayolaWebService {

    def evaluateSparqlQuery(query: String): String = {
        throw new Exception("This method must be overriden!");
    }

    def initialize() = {
        // Start web service actor
        start();
    }

    def act() = {
        receive {
            case x : mutable.ArrayBuffer[_] =>
                if (x.size == 2) {
                    val action = x(0).toString();
                    val parameter = x(1).toString();

                    // Switch by action
                    action match {
                        case "QUERY" =>
                            // Evaluate query
                            val result = mutable.ArrayBuffer[String]();
                            result += "RESULT";
                            result += evaluateSparqlQuery(parameter);

                            // Send result
                            manager ! result;
                    }
                }

            case msg =>
                println("Service: (invalid)" + msg);
                manager ! msg;
        }
    }
}