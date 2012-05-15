package cz.payola.web.client.views.plugins.visual

/**
  * Constants specifying redraw operation of the GraphView.
  */
object RedrawOperation
{
    /**
      * Redraw operation called after selection was changed.
      */
    val Selection = 0

    /**
      * Redraw operation called after vertices were moved.
      */
    val Movement = 1

    /**
      * Redraw operation called during technique processing,
      */
    val Animation = 2
}
