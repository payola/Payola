package cz.payola.web.client.views.graph

import cz.payola.web.client.views._

/**
  * A graph visualisation plugin view.
  * @param name Name of the plugin.
  */
abstract class PluginView(val name: String) extends GraphView with ComposedView
