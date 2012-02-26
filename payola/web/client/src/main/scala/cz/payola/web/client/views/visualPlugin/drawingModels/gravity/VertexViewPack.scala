package cz.payola.web.client.views.visualPlugin.graph.algorithms.gravity

import cz.payola.web.client.views.visualPlugin.graph.VertexView
import cz.payola.web.client.views.visualPlugin.Vector

class VertexViewPack(val value: VertexView)
{
    var force = Vector(0, 0)

    var velocity = Vector(0, 0)
}