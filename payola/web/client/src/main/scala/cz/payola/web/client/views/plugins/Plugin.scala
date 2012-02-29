package cz.payola.web.client.views.plugins

import cz.payola.common.rdf.Graph
import s2js.adapters.js.dom.Element

trait Plugin
{
    def init(graph: Graph, container: Element)

    def update(graph: Graph)

    def redraw()

    def clean()
}
