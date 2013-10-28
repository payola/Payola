package cz.payola.web.client.views.graph.visual.techniques.gravity

import cz.payola.web.client.views.graph.visual.graph._
import cz.payola.web.client.views.algebra.Vector2D

/**
 * Representation of a VertexView object used in GravityTechnique perform routine.
 * @param value contained vertexView
 */
class VertexViewPack(val value: VertexViewElement)
{
    /**
     * Force by which the vertex is being pushed in some direction.
     */
    var force = Vector2D(0, 0)

    /**
     * Velocity representing movement of the vertex in some direction.
     */
    var velocity = Vector2D(0, 0)

    /**
     *
     */
    var currentPosition = value.position
}
