package cz.payola.data

import scala.io.Source
import util.Random
import java.util.{Calendar, Date}
import collection.mutable

class FakeRdfWebService(manager : WebServicesManager) extends IPayolaWebService {
    def evaluateSparqlQuery(query: String): String = {
        val generator: Random = new Random(Calendar.getInstance().getTimeInMillis)

        val sourcePaths: Array[String] = Array("/data.xml", "/data2.xml")
        
        val sourcePath: String = sourcePaths(generator.nextInt(sourcePaths.length))
        val source = Source.fromURL(getClass.getResource(sourcePath));
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
                println ("Rdf (AB): " + x.size);
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
                println("Rdf: (invalid)" + msg);
                manager ! msg;
            //manager ! msg;
        }
    }
}
