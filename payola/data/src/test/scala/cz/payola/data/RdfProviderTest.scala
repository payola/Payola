package cz.payola.data

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import sparql.providers.{AggregateRdfDataProvider, SingleSourceRdfDataProvider}
import sparql.SparqlQueryExecutor

class RdfProviderTest extends FlatSpec with ShouldMatchers
{
    "gwdg" should "gdsgs" in {

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
        val p3 = new SingleSourceRdfDataProvider
        {
            protected def executeSparqlQuery(sparqlQuery: String): String = {
                Thread.sleep(5000)
                println("throwing i")
                throw new Exception("iiiiiiiiiiiiiiiiiii")
            }
        }
        val a = new AggregateRdfDataProvider(List(p1, p2, p3))

        val e = new SparqlQueryExecutor("select ...", a)
        e.start()
    }
}
