package cz.payola.web.client.presenters

import cz.payola.web.client.presenters.components.AnalysisControls
import s2js.adapters.js.browser.document

class Analysis(elementToDrawIn: String, analysisId: String) extends Index(elementToDrawIn)
{
    val controls = new AnalysisControls(analysisId)
    controls.render(document.getElementById("analysis-controls"))

    controls.analysisEvaluated += {
        evt =>
            graph = evt.graph
            plugins.head.clear()
            plugins.head.update(graph.get)
            controls.switchTab
            false
    }
}
