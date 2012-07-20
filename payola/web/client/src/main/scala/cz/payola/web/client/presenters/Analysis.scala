package cz.payola.web.client.presenters

import cz.payola.web.client.presenters.components.AnalysisControls
import s2js.adapters.js.browser.document
import cz.payola.web.client.views.graph.PluginSwitchView
import cz.payola.web.client.Presenter

class Analysis(elementToDrawIn: String, analysisId: String) extends Presenter
{
    val controls = new AnalysisControls(analysisId)

    private val graphView = new PluginSwitchView

    def initialize() {
        controls.render(document.getElementById("analysis-controls"))
        graphView.render(document.getElementById(elementToDrawIn))
    }

    controls.analysisEvaluated += {
        evt =>
            graphView.updateGraph(evt.graph)
            controls.switchTab
            false
    }
}
