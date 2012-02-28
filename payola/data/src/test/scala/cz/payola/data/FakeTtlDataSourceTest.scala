package cz.payola.data

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

class FakeTtlDataSourceTest extends FlatSpec with ShouldMatchers
{
    "Fake data source" should "return content from resources/data.ttl file as query result." in {
        val fakeWS = new FakeTtlDataSource();

        val result = fakeWS.executeSparqlQuery("");

        // Assert valid call result
        result should not be null;
        result.size should not equal (0);
    }
}
