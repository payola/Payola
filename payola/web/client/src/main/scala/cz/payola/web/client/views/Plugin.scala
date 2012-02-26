package cz.payola.web.client.views

import cz.payola.common.rdf.{Vertex, Edge}

trait Plugin
{

    def init()

    def update(vertices: Seq[Vertex], edges: Seq[Edge])

    def redraw()

    def clean()
}
