package cz.payola.web.client.views.plugins.visual.techniques.gravity

import cz.payola.web.client.views.plugins.visual.graph.VertexView
import cz.payola.web.client.views.plugins.visual.Vector

/**
  * Representation of a VertexView object used in GravityTechnique perform routine.
  * @param value contained vertexView
  */
class VertexViewPack(val value: VertexView)
{
    /**
      * Force by which the vertex is being pushed in some direction.
      */
    var force = Vector(0, 0)

    /**
      * Velocity representing movement of the vertex in some direction.
      */
    var velocity = Vector(0, 0)
}