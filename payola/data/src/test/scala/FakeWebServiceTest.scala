package cz.payola.data;

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import cz.payola.data._;

/**
 * User: Ondra Heřmánek
 * Date: 26.12.11, 20:16
 */
class FakeWebServiceTest extends FlatSpec with ShouldMatchers {
    "Fake webservice" should "return content from resources/data.xml file as query result." in {
        val fakeWS = new FakeWebService();

        val result = fakeWS.evaluateSparqlQuery("");

        // Assert valid call result
        result should not be null;
        result.size should not equal (0);
    }
}