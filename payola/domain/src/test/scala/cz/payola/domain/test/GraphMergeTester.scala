package cz.payola.domain.test

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import cz.payola.domain.rdf._
import collection.mutable.ArrayBuffer
import cz.payola.scala2json.JSONSerializer

class GraphMergeTester extends FlatSpec with ShouldMatchers
{
    private def constructFirstGraph: Graph = {
        val n1 = new IdentifiedNode("URI1")
        val n2 = new IdentifiedNode("URI2")
        val n3 = new LiteralNode("Hello", None)

        val e1 = new Edge(n1, n2, "Edge1")
        val e2 = new Edge(n1, n3, "Edge2")

        val edges = new ArrayBuffer[Edge]()
        edges += e1
        edges += e2

        val nodes = new ArrayBuffer[Node]()
        nodes += n1
        nodes += n2
        nodes += n3

        new Graph(nodes, edges)
    }

    private def constructSecondGraph: Graph = {
        val n1 = new IdentifiedNode("URI1")
        val n2 = new IdentifiedNode("URI3")
        val n3 = new LiteralNode("Hello444", None)
        val n4 = new IdentifiedNode("URI2")

        val e1 = new Edge(n1, n2, "EdgeX")
        val e2 = new Edge(n1, n3, "EdgeY")
        val e3 = new Edge(n1, n4, "Edge1")

        val edges = new ArrayBuffer[Edge]()
        edges += e1
        edges += e2
        edges += e3

        val nodes = new ArrayBuffer[Node]()
        nodes += n1
        nodes += n2
        nodes += n3
        nodes += n4

        new Graph(nodes, edges)
    }

    "Merged graph" should "contain all vertices and edges" in {
        val g = constructFirstGraph + constructSecondGraph

        g.containsVertexWithURI("URI1") should be (true)
        g.containsVertexWithURI("URI2") should be (true)
        g.containsVertexWithURI("URI3") should be (true)

        g.containsLiteralVertexWithValue("Hello", None) should be (true)
        g.containsLiteralVertexWithValue("Hello444", None) should be (true)

        g.containsEdgeBetweenNodes(g.getVertexWithURI("URI1").get, g.getVertexWithURI("URI2").get, "Edge1") should be (true)
        g.containsEdgeBetweenNodes(g.getVertexWithURI("URI1").get, g.getVertexWithURI("URI3").get, "EdgeX") should be (true)
    }
}
