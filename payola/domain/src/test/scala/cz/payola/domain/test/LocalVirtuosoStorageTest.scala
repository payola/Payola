package cz.payola.domain.test

import java.sql._
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import cz.payola.domain.virtuoso.PayolaVirtuosoStorage
import cz.payola.domain.rdf._

class LocalVirtuosoStorageTest extends FlatSpec with ShouldMatchers
{
    it should "create a group, add a graph to it and fetch the graph back" in {
        val testXML =
            """<?xml version="1.0"?><rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:cd="http://www.recshop.fake/cd#">
              | <rdf:Description rdf:about="http://www.recshop.fake/cd/Empire Burlesque"><cd:artist>Bob Dylan</cd:artist><cd:country>USA</cd:country>
              | <cd:company>Columbia</cd:company><cd:price>10.90</cd:price><cd:year>1985</cd:year></rdf:Description>
              | <rdf:Description rdf:about="http://www.recshop.fake/cd/Hide your heart"><cd:artist>Bonnie Tyler</cd:artist><cd:country>UK</cd:country>
              | <cd:company>CBS Records</cd:company><cd:price>9.90</cd:price><cd:year>1988</cd:year></rdf:Description></rdf:RDF>""".stripMargin

        val groupName = "my-weird-group"
        val graphName = "my-awesome-graph"

        PayolaVirtuosoStorage.createGroup(groupName)
        PayolaVirtuosoStorage.addGraphToGroup(testXML, graphName, groupName)


        val response = PayolaVirtuosoStorage.selectAllInGroup(groupName)
        println(response)

        val g = Graph(RdfRepresentation.RdfXml, response)

        assume(g.containsVertexWithURI("http://www.recshop.fake/cd/Hide your heart"))
    }
}
