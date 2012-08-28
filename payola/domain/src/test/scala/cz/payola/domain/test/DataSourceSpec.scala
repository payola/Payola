package cz.payola.domain.test

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import cz.payola.common.rdf.IdentifiedVertex
import cz.payola.domain.entities.plugins.DataSource
import cz.payola.domain.entities.plugins.concrete.data.SparqlEndpointFetcher

class DataSourceSpec extends FlatSpec with ShouldMatchers
{
    val instance = (new SparqlEndpointFetcher).createInstance().setParameter(SparqlEndpointFetcher.endpointURLParameter, "http://ld.opendata.cz:8894/sparql")

    val dataSource = DataSource("DBPedia", None, instance)

    "Data source" should "execute sparql queries" in {

        val query = """
            CONSTRUCT {
                ?s a <http://dbpedia.org/ontology/City> .
            } WHERE {
                ?s a <http://dbpedia.org/ontology/City> .
            }
            LIMIT 50
                    """

        val result = dataSource.executeQuery(query)
        assert(!result.isEmpty, "The result is empty.")
        assert(result.vertices.size == result.edges.size + 1, "The graph doesn't conform to the expected result.")
    }

    it should "retrieve neighbours" in {
        val uri = "http://ld.opendata.cz/resource/isvzus.cz/public-contract/9ef681a3-8932-4781-9ae2-1cb4edbdaa8b"
        val neighbourhood = dataSource.getNeighbourhood(uri)
        assert(!neighbourhood.isEmpty, "The neighbourhood is empty.")
    }
}
