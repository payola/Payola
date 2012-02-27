package cz.payola.web.shared

import cz.payola.common.rdf.{ListItem, GraphImpl, IdentifiedVertexImpl, EdgeImpl}

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

    def getRandomList(): ListItem = {
        val i1 = new ListItem(0)
        val i2 = new ListItem(1)
        val i3 = new ListItem(2)
        val i4 = new ListItem(3)
        val i5 = new ListItem(4)

        i1.next = i2
        i2.next = i3
        i3.next = i4
        i4.next = i5

        i5.prev = i4
        i4.prev = i3
        i3.prev = i2
        i2.prev = i1

        i1
    }
}
