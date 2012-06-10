package cz.payola.web.client.events

import cz.payola.common.rdf.Vertex

class VertexUpdateEventArgs(target: Vertex) extends EventArgs[Vertex](target)
{
}
