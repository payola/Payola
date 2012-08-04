package cz.payola.web.client.views.graph.visual

/**
 * Constants specifying redraw operation of the PluginSwitchView.
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

    /**
     * Redraw operation called for whole graph moving.
     */
    val All = 3
}
