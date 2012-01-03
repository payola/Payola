package cz.payola.data

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

/**
 * User: Ondřej Heřmánek
 * Date: 3.1.12, 14:48
 */

class WebServiceManagerTest  extends FlatSpec with ShouldMatchers {
    val manager = new WebServicesManager();

    "WebServiceManager" should "properly init available web services" in {
        manager.initWebServices();

        manager.webServices should not be null;
        manager.webServices.size should not be (0);
    }

    "WebServiceManager" should "obtain Sparql query result from initialized web services" in {
        // Get Sparql query result
        val queryResult = manager.evaluateSparqlQuery("");
        queryResult should not be null;

        // Validate result
        val result = queryResult.getResult();
        result should not be null;
        result.size should not be (0);
    }

    "WebServicesManager" should "parse query result into nodes properly." in {
        // TODO:
    }
}