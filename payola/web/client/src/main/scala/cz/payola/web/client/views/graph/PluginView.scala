package cz.payola.web.client.views.graph

import s2js.adapters.js.dom
import cz.payola.web.client.views._

/**
  * A graph visualisation plugin view.
  * @param name Name of the plugin.
  */
abstract class PluginView(val name: String) extends GraphView with ComposedView
{
    /**
      * Renders the plugin-specific controls.
      * @param toolbar The toolbar element where the controls should be rendered.
      */
    def renderControls(toolbar: dom.Element) { }

    /**
      * Destroys the plugin-specific controls.
      */
    def destroyControls() { }
}
