package cz.payola.common.rdf

class VertexGroup(val uri: String, var content: Seq[VertexLink]) extends VertexLink(uri)
{

    override def equals(other: Any): Boolean = {
        other match {
            case vg: VertexGroup =>
                var eq = true
                content.foreach{con => eq = eq && vg.contains(con)}
                eq
            case _ => false
        }
    }

    override def toString = uri

    def contains(vertex: Vertex): Boolean = {
        content.exists(_.equals(vertex))
    }
}
