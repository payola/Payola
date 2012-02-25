package cz.payola.data

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

class FakeRdfWebServiceTest extends FlatSpec with ShouldMatchers
{
    "Fake webservice" should "return content from resources/data.xml file as query result." in {
        val fakeWS = new FakeRdfWebService(new WebServicesManager());

        val result = fakeWS.evaluateSparqlQuery("");

        // Assert valid call result
        result should not be null;
        result.size should not equal (0);
    }
}