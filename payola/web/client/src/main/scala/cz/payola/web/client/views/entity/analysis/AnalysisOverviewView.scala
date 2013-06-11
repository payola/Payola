package cz.payola.web.client.views.entity.analysis

import cz.payola.web.client.presenters.components.AnalysisControls
import cz.payola.web.client.views.ComposedView
import cz.payola.common.entities.Analysis
import cz.payola.web.client.models.PrefixApplier

class AnalysisOverviewView(analysis: Analysis, timeoutSeconds: Int, prefixApplier: PrefixApplier) extends ComposedView
{
    val controls = new AnalysisControls(timeoutSeconds)

    val analysisVisualizer = new ReadOnlyAnalysisVisualizer(analysis, prefixApplier)

    def createSubViews = List(controls, analysisVisualizer)
}
