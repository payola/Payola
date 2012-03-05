package cz.payola.web.client.views.plugins.textual

import cz.payola.web.client.views.plugins.Plugin
import cz.payola.common.rdf.Graph
import s2js.adapters.js.dom.Element

/**
  * Representation of text based output drawing plugin
  */
abstract class TextPlugin extends Plugin
{
    def init(graph: Graph, container: Element)

    def update(graph: Graph)

    def redraw()

    def clean()
}
