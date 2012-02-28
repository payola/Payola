package cz.payola.data

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

class FakeRdfDataSourceTest extends FlatSpec with ShouldMatchers
{
    "Fake data source" should "return content from resources/data.xml file as query result." in {
        val fakeWS = new FakeRdfDataSource();

        val result = fakeWS.executeSparqlQuery("");

        // Assert valid call result
        result should not be null;
        result.size should not equal (0);
    }
}
