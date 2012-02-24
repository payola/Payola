package cz.payola.web.client.views.graph.algorithms.gravity

import cz.payola.web.client.views.graph.VertexView
import cz.payola.web.client.views.Vector

class VertexViewPack(val value: VertexView)
{
    var force = Vector(0, 0)

    var velocity = Vector(0, 0)
}