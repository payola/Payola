package cz.payola.data.rdf

import messages.QueryMessage
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import providers._
import configurations.SparqlEndpointConfiguration
import cz.payola.domain.entities.analyses.messages.{QueryExecutionResult, QueryMessage}
import cz.payola.domain.entities.analyses.QueryResult
import cz.payola.domain.entities.analyses.executors.{AggregateQueryExecutor, SingleQueryExecutor}

class QueryExecutorSpecs extends FlatSpec with ShouldMatchers
{
    // Sparql provider with default configuration = DBPedia
    val sparqlProvider = new SparqlEndpointConfiguration().createProvider
    val fakeProvider = new FakeDataProvider
    val fakeTtlProvider = new FakeTtlDataProvider
    val exceptionProvider = new SingleQueryExecutor
    {
        protected def executeQuery(sparqlQuery: String): String = {
            Thread.sleep(1000)
            throw new Exception
        }
    }
    val simpleProvider = new SingleQueryExecutor
    {
        protected def executeQuery(sparqlQuery: String): String = {
            Thread.sleep(3000)
            "result"
        }
    }
    val timeout = 20000

    // This query works with Virtuoso data source
    val query = "select distinct ?Concept where {[] a ?Concept} LIMIT 100";

    "Query executor" should "execute query on simple single data provider" in {
        val agregateProvider = new AggregateQueryExecutor(List(simpleProvider))
        val executor = new DataProvider(agregateProvider, timeout)
        executor.start()

        // Execute the query.
        val result = executor !? QueryMessage(query)
        val r = result match {
            case r: QueryResult => r
            case _ => QueryResult.empty
        }

        assert(r.expectedResultCount == 1)
        assert(r.data.length == 1)
        assert(r.errors.length == 0)
    }

    it should "execute query on remote single data provider" in {
        val agregateProvider = new AggregateQueryExecutor(List(sparqlProvider))
        val executor = new DataProvider(agregateProvider, timeout)
        executor.start()

        // Execute the query.
        val result = executor !? QueryMessage(query)
        val r = result match {
            case r: QueryResult => r
            case _ => QueryResult.empty
        }

        assert(r.expectedResultCount == 1)
        assert(r.data.length == 1)
        assert(r.errors.length == 0)
    }

    it should "handle exception in simple single data provider" in {
        val agregateProvider = new AggregateQueryExecutor(List(exceptionProvider))
        val executor = new DataProvider(agregateProvider, timeout)
        executor.start()

        // Execute the query.
        val result = executor !? QueryMessage(query)
        val r = result match {
            case r: QueryResult => r
            case _ => QueryResult.empty
        }

        assert(r.expectedResultCount == 1)
        assert(r.data.length == 0)
        assert(r.errors.length == 1)
    }

    it should "support query timeout" in {
        val agregateProvider = new AggregateQueryExecutor(List(simpleProvider))
        val executor = new DataProvider(agregateProvider, 2000)
        executor.start()

        // Execute the query.
        val result = executor !? QueryMessage(query)
        val r = result match {
            case r: QueryResult => r
            case _ => QueryResult.empty
        }

        assert(r.expectedResultCount == 1)
        assert(r.data.length == 0)
        assert(r.errors.length == 0)
    }

    it should "execute query on simple aggregate data provider" in {
        val providers = List(simpleProvider, fakeProvider, fakeTtlProvider, exceptionProvider)
        val agregateProvider = new AggregateQueryExecutor(providers)
        val executor = new DataProvider(agregateProvider, 2000)
        executor.start()

        // Execute the query.
        val result = executor !? QueryMessage(query)
        val r = result match {
            case r: QueryResult => r
            case _ => QueryResult.empty
        }

        assert(r.expectedResultCount == 4)
        assert(r.data.length == 2)
        assert(r.errors.length == 1)
    }
}
