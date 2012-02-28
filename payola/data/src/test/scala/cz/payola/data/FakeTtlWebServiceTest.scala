package cz.payola.data

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

class FakeTtlWebServiceTest extends FlatSpec with ShouldMatchers
{
    "Fake webservice" should "return content from resources/data.ttl file as query result." in {
        val fakeWS = new FakeTtlWebService(new WebServicesManager());

        val result = fakeWS.evaluateSparqlQuery("");

        // Assert valid call result
        result should not be null;
        result.size should not equal (0);
    }
}
