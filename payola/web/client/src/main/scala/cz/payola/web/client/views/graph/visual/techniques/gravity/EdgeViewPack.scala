package cz.payola.web.client.views.graph.visual.techniques.gravity

import cz.payola.web.client.views.graph.visual.graph.EdgeView

/**
 * Representation of a EdgeView object used in GravityTechnique perform routine.
 * @param value contained edgeView
 * @param originVertexViewPack origin of the original vertex
 * @param destinationVertexViewPack destination of the original vertex
 */
class EdgeViewPack(val value: EdgeView,
    val originVertexViewPack: VertexViewPack, val destinationVertexViewPack: VertexViewPack)
{}
