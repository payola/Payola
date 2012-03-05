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
      * @param graph to visualise
      * @param container where to visualise
      */
    def init(graph: Graph, container: Element)

    /**
      * Add a graph to the visualised structure.
      * @param graph
      */
    def update(graph: Graph)

    /**
      * Draws the contained graph.
      */
    def redraw()

    /**
      * Purges the contained graph and contents of this plugin.
      */
    def clean()
}
