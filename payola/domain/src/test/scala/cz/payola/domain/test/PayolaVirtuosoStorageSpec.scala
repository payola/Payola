package cz.payola.domain.test

import java.sql._
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import cz.payola.domain.virtuoso.PayolaVirtuosoStorage
import cz.payola.domain.entities.plugins.concrete.data.PayolaStorage

class PayolaVirtuosoStorageSpec extends FlatSpec with ShouldMatchers
{
    val testXML =
        """|<?xml version="1.0"?>
           |<rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:cd="http://www.recshop.fake/cd#">
           |    <rdf:Description rdf:about="http://www.recshop.fake/cd/Empire Burlesque">
           |        <cd:artist>Bob Dylan</cd:artist>
           |        <cd:country>USA</cd:country>
           |        <cd:company>Columbia</cd:company>
           |        <cd:price>10.90</cd:price>
           |        <cd:year>1985</cd:year>
           |    </rdf:Description>
           |        <rdf:Description rdf:about="http://www.recshop.fake/cd/Hide your heart">
           |        <cd:artist>Bonnie Tyler</cd:artist>
           |        <cd:country>UK</cd:country>
           |        <cd:company>CBS Records</cd:company>
           |        <cd:price>9.90</cd:price>
           |        <cd:year>1988</cd:year>
           |    </rdf:Description>
           |</rdf:RDF>""".stripMargin

    val groupName = "test-group"

    val graphName = "test-graph"

    it should "create a group, add a graph to it, fetch the graph back and delete everything afterwards" in {
        PayolaVirtuosoStorage.createGroup(groupName)
        PayolaVirtuosoStorage.addGraphToGroup(graphName, testXML, groupName)

        val g = PayolaVirtuosoStorage.executeSPARQLQuery("CONSTRUCT { ?s ?p ?o } WHERE {?s ?p ?o.}", groupName)
        assert(g.containsVertexWithURI("http://www.recshop.fake/cd/Hide your heart"),
            "The query over a group didn't return the expected data.")

        PayolaVirtuosoStorage.deleteGraph(graphName)
        PayolaVirtuosoStorage.deleteGroup(groupName)
    }
}
