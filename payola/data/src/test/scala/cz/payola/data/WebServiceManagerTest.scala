package cz.payola.data

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

class WebServiceManagerTest extends FlatSpec with ShouldMatchers
{
    val manager = new WebServicesManager();

    "WebServiceManager" should "properly init available web services" in {
        manager.initialize();

        manager.webServices should not be null;
        manager.webServices.size should not be (0);
    }

    "WebServiceManager" should "obtain Sparql query result from initialized web services" in {
        // Get Sparql query result
        val query = "select distinct ?Concept where {[] a ?Concept} LIMIT 100";
        val queryResult = manager.evaluateSparqlQuery(query);
        queryResult should not be null;

        // Validate result
        val rdf = queryResult.rdf;
        rdf should not be null;
        rdf.size should not be (0);

        val ttl = queryResult.ttl;
        ttl should not be null;
        ttl.size should not be (0);
    }

    "WebServicesManager" should "parse query result into nodes properly." in {
        // TODO:
    }
}