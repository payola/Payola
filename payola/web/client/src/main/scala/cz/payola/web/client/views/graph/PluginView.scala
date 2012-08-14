package cz.payola.web.client.views.graph

import s2js.adapters.html
import cz.payola.web.client.views._
import cz.payola.web.client.views.elements._

/**
 * A graph visualization plugin view.
 * @param name Name of the plugin.
 */
abstract class PluginView(val name: String) extends GraphView with ComposedView
{
    /**
     * Renders the plugin-specific controls.
     * @param toolbar The toolbar element where the controls should be rendered.
     */
    def renderControls(toolbar: html.Element) {}

    /**
     * Destroys the plugin-specific controls.
     */
    def destroyControls() {}

    protected def renderMessage(parent: html.Element, message: String, description: String = "") {
        new Div(List(new Text(message)), "plugin-message large").render(parent)
        new Div(List(new Text(description)), "plugin-message small").render(parent)
    }
}
