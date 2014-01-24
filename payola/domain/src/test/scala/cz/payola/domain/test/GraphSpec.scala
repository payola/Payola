package cz.payola.domain.test

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import cz.payola.common.rdf._
import cz.payola.domain.rdf._
import cz.payola.domain.rdf.PayolaGraph
import cz.payola.common.rdf.LiteralVertex

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

    it should "be parsable from the trig format" in {
        val trig = """
            @prefix :        <#> .
            @prefix odcs:    <http://opendata.cz/infrastructure/odcleanstore/> .
            @prefix w3p:     <http://purl.org/provenance#> .
            @prefix rdfs:    <http://www.w3.org/2000/01/rdf-schema#> .
            @prefix rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
            @prefix dc:      <http://purl.org/dc/elements/1.1/> .
            @prefix ns2:     <http://dbpedia.org/ontology/> .

            <http://opendata.cz/infrastructure/odcleanstore/query/results/1> {
              <http://dbpedia.org/resource/Berlin> rdfs:label "Berlin"@en .
            }

            <http://opendata.cz/infrastructure/odcleanstore/query/results/2> {
              <http://dbpedia.org/resource/Berlin> ns2:populationTotal "3420768"^^<http://www.w3.org/2001/XMLSchema#int> .
            }

            <http://opendata.cz/infrastructure/odcleanstore/query/metadata/> {
              <http://opendata.cz/infrastructure/odcleanstore/query/results/1>
                odcs:quality 0.92 ;
                w3p:source <http://opendata.cz/infrastructure/odcleanstore/data/e0cdc9d7-e2d8-4bde-8492-810fdf42df63> ;
                w3p:source <http://opendata.cz/infrastructure/odcleanstore/data/b68e21f7-363f-4bfd-98c8-126703ec97c5> .

              <http://opendata.cz/infrastructure/odcleanstore/query/results/2>
                odcs:quality 0.8966325468133597 ;
                w3p:source <http://opendata.cz/infrastructure/odcleanstore/data/b68e21f7-363f-4bfd-98c8-126703ec97c5> .

              <http://opendata.cz/infrastructure/odcleanstore/data/e0cdc9d7-e2d8-4bde-8492-810fdf42df63>
                odcs:score 0.9 ;
                w3p:insertedAt "2012-04-01 12:34:56.0"^^<http://www.w3.org/2001/XMLSchema#dateTime> ;
                w3p:source <http://dbpedia.org/page/Berlin> ;
                w3p:publishedBy <http://dbpedia.org/> ;
                odcs:publisherScore 0.9 .

              <http://opendata.cz/infrastructure/odcleanstore/data/b68e21f7-363f-4bfd-98c8-126703ec97c5>
                odcs:score 0.8 ;
                w3p:insertedAt "2012-04-04 12:34:56.0"^^<http://www.w3.org/2001/XMLSchema#dateTime> ;
                w3p:source <http://linkedgeodata.org/page/node240109189> .

              <http://localhost:8087/uri?uri=http%3A%2F%2Fdbpedia.org%2Fpage%2FBerlin>
                a odcs:QueryResponse ;
                dc:title "URI search: http://dbpedia.org/page/Berlin" ;
                dc:date "2012-08-01T10:20:30+01:00" ;
                odcs:totalResults 2 ;
                odcs:query "http://dbpedia.org/page/Berlin" ;
                odcs:result <http://opendata.cz/infrastructure/odcleanstore/query/results/1> ;
                odcs:result <http://opendata.cz/infrastructure/odcleanstore/query/results/2> .
            }"""
        val graph = PayolaGraph(RdfRepresentation.Trig, trig)
        assert(graph.vertices.nonEmpty)
        assert(graph.edges.nonEmpty)
    }

    private def graph1: PayolaGraph = {
        val n1 = new IdentifiedVertex("URI1")
        val n2 = new IdentifiedVertex("URI2")
        val n3 = new LiteralVertex("Hello", None)

        val e1 = new Edge(n1, n2, "Edge1")
        val e2 = new Edge(n1, n3, "Edge2")

        new PayolaGraph(List(n1, n2, n3), List(e1, e2))
    }

    private def graph2: PayolaGraph = {
        val n1 = new IdentifiedVertex("URI1")
        val n2 = new IdentifiedVertex("URI3")
        val n3 = new LiteralVertex("Hello444", None)
        val n4 = new IdentifiedVertex("URI2")

        val e1 = new Edge(n1, n2, "EdgeX")
        val e2 = new Edge(n1, n3, "EdgeY")
        val e3 = new Edge(n1, n4, "Edge1")

        new PayolaGraph(List(n1, n2, n3, n4), List(e1, e2, e3))
    }
}
