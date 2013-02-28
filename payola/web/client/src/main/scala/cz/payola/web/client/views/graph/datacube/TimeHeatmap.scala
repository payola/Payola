package cz.payola.web.client.views.graph.datacube

import cz.payola.web.client.views.graph.PluginView
import cz.payola.web.client.views.elements.Div
import cz.payola.common.rdf.Graph

class TimeHeatmap extends PluginView("Time heatmap") {

    val mapCanvas = new Div(List())
    mapCanvas.setAttribute("id","map_canvas")

    override def updateGraph(graph: Option[Graph], contractLiterals: Boolean = true) {

    }

    def createSubViews = {
        //new google.maps.Map()

        List(mapCanvas)
    }
}
