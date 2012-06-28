package cz.payola.domain.test

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import cz.payola.domain.virtuoso.LocalVirtuosoStorage
import cz.payola.domain.rdf.Graph

class LocalVirtuosoStorageTest extends FlatSpec with ShouldMatchers
{
    it should "create a group, add a graph to it and fetch the graph back" in {
        val testXML =
            """<?xml version="1.0"?><rdf:RDFxmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"xmlns:cd="http://www.recshop.fake/cd#">
              | <rdf:Descriptionrdf:about="http://www.recshop.fake/cd/Empire Burlesque"><cd:artist>Bob Dylan</cd:artist><cd:country>USA</cd:country>
              | <cd:company>Columbia</cd:company><cd:price>10.90</cd:price><cd:year>1985</cd:year></rdf:Description>
              | <rdf:Descriptionrdf:about="http://www.recshop.fake/cd/Hide your heart"><cd:artist>Bonnie Tyler</cd:artist><cd:country>UK</cd:country>
              | <cd:company>CBS Records</cd:company><cd:price>9.90</cd:price><cd:year>1988</cd:year></rdf:Description></rdf:RDF>""".stripMargin

        val groupName = "my-weird-group"
        val graphName = "my-awesome-graph"

        LocalVirtuosoStorage.createGroup(groupName)
        LocalVirtuosoStorage.addGraphToGroup(testXML, graphName, groupName)


        val response = LocalVirtuosoStorage.selectAllInGroup("mygroup-id")
        println(response)

        val g = Graph(response)
        println(g.containsVertexWithURI("http://www.recshop.fake/cd/Hide your heart"))
    }
}
