package cz.payola.web.client.views.plugins.textual

import cz.payola.web.client.views.plugins.Plugin
import cz.payola.common.rdf.{Vertex, Edge}

abstract class TextPlugin extends Plugin
{
    def init()

    def update(vertices: Seq[Vertex], edges: Seq[Edge])

    def redraw()

    def clean()
}
