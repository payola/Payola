package cz.payola.web.client.views.plugins

import cz.payola.common.rdf.Graph
import s2js.adapters.js.dom.Element

/**
  * Representation of a visualisation plug-in
  */
trait Plugin
{
    /**
      * Prepares the contents for proper drawing and data visualisation.
      * @param container where to visualise
      */
    def init(container: Element)

    /**
      * Updates the current graph of the plugin and re-runs the visualisation
      * @param graph to be added to the current graph
      */
    def update(graph: Graph)

    /**
      * Removes the current graph from the plugins memory and resets the visualisation.
      */
    def clear()

    /**
      * Draws the contained graph.
      */
    def redraw()

    /**
      * Purges the contained graph, contents of this plugin and removes the created HTML elements form its parents.
      * (Puts the plugin into initialization-required state, that calling init(..) would reset the plugin to its
      * original state)
      */
    def destroy()

    /**
      * Short description name of this plugin
      */
    def getName: String
}
