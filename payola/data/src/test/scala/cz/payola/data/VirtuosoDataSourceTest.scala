package cz.payola.data

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

class VirtuosoDataSourceTest extends FlatSpec with ShouldMatchers
{
    val fakeWS = new VirtuosoDataSource();

    "Virtuoso data source" should "return proper query result." in {

        val goodQuery = "select distinct ?Concept where {[] a ?Concept} LIMIT 100";
        val result = fakeWS.executeSparqlQuery(goodQuery);

        // Assert valid call result
        result should not be null;
        result.size should not equal (0);
    }

    "Virtuoso data source" should "throw an exception for invalid query" in {
        evaluating(fakeWS.executeSparqlQuery("")) should produce [Exception]
    }
}
