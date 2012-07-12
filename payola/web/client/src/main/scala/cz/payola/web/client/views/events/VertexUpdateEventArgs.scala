package cz.payola.web.client.views.events

import cz.payola.common.rdf.Vertex
import cz.payola.web.client.events.EventArgs

class VertexUpdateEventArgs(target: Vertex) extends EventArgs[Vertex](target)
