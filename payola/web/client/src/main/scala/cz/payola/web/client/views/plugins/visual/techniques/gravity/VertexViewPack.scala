package cz.payola.web.client.views.plugins.visual.techniques.gravity

import cz.payola.web.client.views.plugins.visual.graph.VertexView
import cz.payola.web.client.views.plugins.visual.Vector

class VertexViewPack(val value: VertexView)
{
    var force = Vector(0, 0)

    var velocity = Vector(0, 0)
}