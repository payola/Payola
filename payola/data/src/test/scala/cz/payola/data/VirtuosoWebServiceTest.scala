package cz.payola.data

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

class VirtuosoWebServiceTest extends FlatSpec with ShouldMatchers
{
    "Virtuoso webservice" should "return proper query result." in {
        val fakeWS = new VirtuosoWebService(new WebServicesManager());
        fakeWS.initialize();

        val goodQuery = "select distinct ?Concept where {[] a ?Concept} LIMIT 100";
        var result = fakeWS.evaluateSparqlQuery(goodQuery);

        // Assert valid call result
        result should not be null;
        result.size should not equal (0);

        val badQuery = "";
        result = fakeWS.evaluateSparqlQuery(badQuery);

        // Assert invalid call result
        result should not be null;
        result.size should equal (0);
    }
}