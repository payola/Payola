package cz.payola.data.rdf

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import providers._

class QueryExecutorSpecs extends FlatSpec with ShouldMatchers
{
    val simpleProvider = new SingleDataProvider
    {
        protected def executeQuery(sparqlQuery: String): String = {
            Thread.sleep(3000)
            "result"
        }
    }
    val fakeProvider = new FakeDataProvider
    val fakeTtlProvider = new FakeTtlDataProvider
    val virtuosoProvider = new VirtuosoDataProvider
    val exceptionProvider = new SingleDataProvider
    {
        protected def executeQuery(sparqlQuery: String): String = {
            Thread.sleep(1000)
            throw new Exception
        }
    }

    // This query works with Virtuoso data source
    val query = "select distinct ?Concept where {[] a ?Concept} LIMIT 100";

    "Query executor" should "execute query on simple single data provider" in {
        val result = QueryExecutor.executeQuery(simpleProvider, query)
        assert(result.expectedResultCount == 1)
        assert(result.data.length == 1)
        assert(result.errors.length == 0)
    }

    it should "execute query on remote single data provider" in {
        val result = QueryExecutor.executeQuery(virtuosoProvider, query)
        assert(result.expectedResultCount == 1)
        assert(result.data.length == 1)
        assert(result.errors.length == 0)
    }

    it should "handle exception in simple single data provider" in {
        val result = QueryExecutor.executeQuery(exceptionProvider, query)
        assert(result.expectedResultCount == 1)
        assert(result.data.length == 0)
        assert(result.errors.length == 1)
    }

    it should "support query timeout" in {
        val result = QueryExecutor.executeQuery(simpleProvider, query, 2000)
        assert(result.expectedResultCount == 1)
        assert(result.data.length == 0)
        assert(result.errors.length == 0)
    }

    it should "execute query on simple aggregate data provider" in {
        val providers = List(simpleProvider, fakeProvider, fakeTtlProvider, exceptionProvider)
        val result = QueryExecutor.executeQuery(new AggregateDataProvider(providers), query, 2000)
        assert(result.expectedResultCount == 4)
        assert(result.data.length == 2)
        assert(result.errors.length == 1)
    }
}
