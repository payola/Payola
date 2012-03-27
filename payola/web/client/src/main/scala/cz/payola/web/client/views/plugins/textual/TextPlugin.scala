package cz.payola.web.client.views.plugins.textual

import cz.payola.web.client.views.plugins.Plugin
import cz.payola.common.rdf.Graph
import s2js.adapters.js.dom.Element

/**
  * Representation of text based output drawing plugin
  */
abstract class TextPlugin extends Plugin
{
    protected var parentElement: Option[Element] = None
    
    protected var graphModel: Option[Graph] = None
    
    def init(container: Element) {
        parentElement = Some(container)
    }

    def update(graph: Graph) {

        graphModel = Some(graph)
    }

    def redraw()

    def clean()
}
