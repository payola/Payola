package cz.payola.web.shared

import cz.payola.common.rdf.{GraphImpl, IdentifiedVertexImpl, EdgeImpl}

@scala.remote
object RpcTestRemote
{
    def foo(bar: Int, baz: String): Int = bar * baz.length

    def getRandomGraph(): GraphImpl = {
        val v1 = new IdentifiedVertexImpl("http://payola.cz/ondra")
        val v2 = new IdentifiedVertexImpl("http://payola.cz/honza")
        val vertices = List(v1,v2)

        val e1 = new EdgeImpl(v1,v2,"http://payola.cz/codesWith")
        val edges = List(e1)

        new GraphImpl(vertices, edges)
    }
}
