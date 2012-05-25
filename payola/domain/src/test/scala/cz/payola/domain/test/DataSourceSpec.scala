package cz.payola.domain.test

import cz.payola.domain.entities.analyses._
import plugins.data.SparqlEndpoint
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import cz.payola.common.rdf.IdentifiedVertex

class DataSourceSpec extends FlatSpec with ShouldMatchers
{
    val instance = (new SparqlEndpoint).createInstance().setParameter("EndpointURL", "http://dbpedia.org/sparql")

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
        val uri = "http://dbpedia.org/resource/Prague"
        val neighbourhood = dataSource.getNeighbourhood(uri)
        assert(!neighbourhood.isEmpty, "The neighbourhood is empty.")

        val neighbour = neighbourhood.vertices.collect {
            case i: IdentifiedVertex if i.uri != uri && i.uri.startsWith("http://dbpedia.org/resource/Czech") => i
        }.headOption
        assert(neighbour.nonEmpty, "The neighbourhood doesn't contain the expected vertex.")

        val distantNeighbourhood = dataSource.getNeighbourhood(neighbour.get.uri)
        assert(!distantNeighbourhood.isEmpty, "The neighbouhood of the first neighbour is empty.")
    }
}
