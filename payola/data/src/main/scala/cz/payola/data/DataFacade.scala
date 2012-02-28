package cz.payola.data

import cz.payola.common.rdf.Graph
import model.graph.{RDFGraph, RDFIdentifiedNode, RDFEdge}

class DataFacade {

    def getGraph(uri: String) : Graph = {

        val vPayola = new RDFIdentifiedNode("http://payola.cz")
        val vOndraH = new RDFIdentifiedNode("http://payola.cz/coders/OndraHermanek")
        val vOndraK = new RDFIdentifiedNode("http://payola.cz/coders/OndraKudlacek")
        val vHonza = new RDFIdentifiedNode("http://payola.cz/coders/HonzaSiroky")
        val vKrystof = new RDFIdentifiedNode("http://payola.cz/coders/KrystofVasa")
        val vJirka = new RDFIdentifiedNode("http://payola.cz/coders/JirkaHelmich")

        val e1 = new RDFEdge(vPayola, vOndraH, "http://payola.cz/codedBy")
        val e2 = new RDFEdge(vPayola, vOndraK, "http://payola.cz/codedBy")
        val e3 = new RDFEdge(vPayola, vHonza, "http://payola.cz/codedBy")
        val e4 = new RDFEdge(vPayola, vKrystof, "http://payola.cz/codedBy")
        val e5 = new RDFEdge(vPayola, vJirka, "http://payola.cz/codedBy")

        new RDFGraph(
            List(vPayola,vOndraH,vOndraK,vHonza,vKrystof,vJirka),
            List(e1,e2,e3,e4,e5)
        )

    }

}