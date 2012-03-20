package cz.payola.data

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import sparql.messages.QueryMessage
import sparql.providers.{AggregateDataProvider, SingleDataProvider}
import sparql.QueryExecutor
import org.scalatest.Assertions._

class QueryExecutorTests extends FlatSpec with ShouldMatchers
{
    "Query executor" should "gather data from all its sources" in {
        val p1 = new FakeDataProvider();
        val p2 = new FakeTtlDataProvider();
        val p3 = new VirtuosoDataProvider();
        val p4 = new SingleDataProvider
        {
            protected def executeQuery(sparqlQuery: String): String = {
                Thread.sleep(1000)
                throw new Exception
            }
        }
        val p5 = new SingleDataProvider
        {
            protected def executeQuery(sparqlQuery: String): String = {
                Thread.sleep(3000)
                "result"
            }
        }

        // This query works with Virtuoso data source
        val query = "select distinct ?Concept where {[] a ?Concept} LIMIT 100";

        val result1 = QueryExecutor.executeQuery(new AggregateDataProvider(List(p1, p2, p3, p4)), query)
        assert(result1.expectedResultCount == 4)
        assert(result1.data.length == 3)
        assert(result1.errors.length == 1)

        val result2 = QueryExecutor.executeQuery(new AggregateDataProvider(List(p1, p2, p3, p4, p5)), query, 2000)
        assert(result2.expectedResultCount == 5)
        assert(result2.data.length == 3)
        assert(result2.errors.length == 1)
    }
}
