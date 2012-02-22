package cz.payola.data

import scala.io.Source
import collection.mutable

class FakeTtlWebService(manager : WebServicesManager) extends IPayolaWebService {
    def evaluateSparqlQuery(query: String): String = {
        val source = Source.fromURL(getClass.getResource("/data.ttl"));
        val result = new StringBuilder();

        source.foreach(char => result.append(char));

        return result.toString();
    }

    def initialize() = {
        // Start actor for parallel query processing
        start();
    }

    def act() = {
        receive {
            case x : mutable.ArrayBuffer[String] =>
                println ("Ttl (AB): " + x.size);
                if (x.size == 2) {
                    val action = x(0);
                    val parameter = x(1);

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
                println("Ttl: (invalid)" + msg);
                manager ! msg;
            //manager ! msg;
        }
    }
}