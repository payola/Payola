package cz.payola.domain.test

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import cz.payola.common.rdf._
import cz.payola.domain.rdf.Graph

class GraphSpec extends FlatSpec with ShouldMatchers
{
    "Merged graph" should "contain all original vertices and edges with no duplicities" in {
        val g = graph1 + graph2

        g.vertices.length should be (5)
        g.edges.length should be (4)

        g.containsVertexWithURI("URI1") should be (true)
        g.containsVertexWithURI("URI2") should be (true)
        g.containsVertexWithURI("URI3") should be (true)
        g.containsVertexWithValue("Hello", None) should be (true)
        g.containsVertexWithValue("Hello444", None) should be (true)

        g.containsEdge(g.getVertexWithURI("URI1").get, g.getVertexWithURI("URI2").get, "Edge1") should be (true)
        g.containsEdge(g.getVertexWithURI("URI1").get, g.getVertexWithURI("URI3").get, "EdgeX") should be (true)
    }

    "Graph" should "be queryable using SPARQL query" in {
        val original = graph1
        val result = original.executeSPARQLQuery("CONSTRUCT { ?s ?p ?o } WHERE { ?s ?p ?o }")
        assert((original.vertices.toSet ++ result.vertices) == original.vertices.toSet,
            "The result doesn't contain all expected vertices.")
        assert((original.edges.toSet ++ result.edges) == original.edges.toSet,
            "The result doesn't contain all expected edges.")
    }

    private def graph1: Graph = {
        val n1 = new IdentifiedVertex("URI1")
        val n2 = new IdentifiedVertex("URI2")
        val n3 = new LiteralVertex("Hello", None)

        val e1 = new Edge(n1, n2, "Edge1")
        val e2 = new Edge(n1, n3, "Edge2")

        new Graph(List(n1, n2, n3), List(e1, e2))
    }

    private def graph2: Graph = {
        val n1 = new IdentifiedVertex("URI1")
        val n2 = new IdentifiedVertex("URI3")
        val n3 = new LiteralVertex("Hello444", None)
        val n4 = new IdentifiedVertex("URI2")

        val e1 = new Edge(n1, n2, "EdgeX")
        val e2 = new Edge(n1, n3, "EdgeY")
        val e3 = new Edge(n1, n4, "Edge1")

        new Graph(List(n1, n2, n3, n4), List(e1, e2, e3))
    }
}
