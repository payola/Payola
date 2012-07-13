package cz.payola.web.client.views.plugins.visual.techniques.gravity

import cz.payola.web.client.views.plugins.visual.graph.VertexView
import cz.payola.web.client.views.Vector2D

/**
  * Representation of a VertexView object used in GravityTechnique perform routine.
  * @param value contained vertexView
  */
class VertexViewPack(val value: VertexView) {

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
