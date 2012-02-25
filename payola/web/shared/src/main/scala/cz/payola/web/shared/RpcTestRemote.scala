package cz.payola.web.shared

import cz.payola.common.rdf.{Graph}
import cz.payola.data.model.graph._

@scala.remote
object RpcTestRemote
{
    def foo(bar: Int, baz: String): Int = bar * baz.length
    def getRandomGraph:Graph = {
        val v1 = new RDFNode("http://payola.cz/ondra")
        val v2 = new RDFNode("http://payola.cz/honza")
        val vertices = List(v1,v2)

        val e1 = new RDFEdge(v1,v2,"http://payola.cz/codesWith")
        val edges = List(e1)

        new RDFGraph(vertices, edges)
    }
}
