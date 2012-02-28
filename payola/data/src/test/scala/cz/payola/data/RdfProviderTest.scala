package cz.payola.data

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import sparql.providers.{AggregateRdfDataProvider, SingleSourceRdfDataProvider}
import sparql.SparqlQueryExecutor

class RdfProviderTest extends FlatSpec with ShouldMatchers
{
    "Rdf provider" should "gather data from all its sources" in {

        /*
        val p1 = new SingleSourceRdfDataProvider
        {
            protected def executeSparqlQuery(sparqlQuery: String): String = {
                Thread.sleep(2000)
                println("returning a")
                return "aaaaaaaaaaaaaaaaaaaaaaaaaaa"
            }
        }
        val p2 = new SingleSourceRdfDataProvider
        {
            protected def executeSparqlQuery(sparqlQuery: String): String = {
                Thread.sleep(8000)
                println("returning b")
                return "bbbbbbbbbbbbbbbbbbbbbbbb"
            }
        }

        */
        val p1 = new FakeRdfDataSource();
        val p2 = new FakeTtlDataSource();
        val p3 = new VirtuosoDataSource();
        val p4 = new SingleSourceRdfDataProvider
        {
            protected def executeSparqlQuery(sparqlQuery: String): String = {
                Thread.sleep(5000)
                println("throwing i")
                throw new Exception("iiiiiiiiiiiiiiiiiii")
            }
        }

        val p5 = new SingleSourceRdfDataProvider
        {
            protected def executeSparqlQuery(sparqlQuery: String): String = {
                Thread.sleep(30000)
                println("returning t")
                return "ttttttttttttttttttt"
            }
        }

        // This query works with Virtuoso data source
        val query = "select distinct ?Concept where {[] a ?Concept} LIMIT 100";

        val a1 = new AggregateRdfDataProvider(List(p1, p2, p3, p4))
        val a2 = new AggregateRdfDataProvider(List(p1, p2, p3, p4, p5))

        // OK
        val e1 = new SparqlQueryExecutor(query, a1)
        e1.start()

        // Should finish on timeout
        val e2 = new SparqlQueryExecutor(query, a2)
        e2.start()
    }
}
