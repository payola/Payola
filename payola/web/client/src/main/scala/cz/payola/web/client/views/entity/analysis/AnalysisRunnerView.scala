package cz.payola.web.client.views.entity.analysis

import cz.payola.web.client.views.ComposedView
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.bootstrap.Tabs
import cz.payola.common.entities.Analysis
import cz.payola.web.client.models.PrefixApplier

class AnalysisRunnerView(analysis: Analysis, prefixApplier: PrefixApplier) extends ComposedView
{
    val overviewView = new AnalysisOverviewView(analysis, prefixApplier)

    val resultsView = new Div

    val tabs = new Tabs(List(
        ("Overview", overviewView),
        ("Results", resultsView)
    ), "analysis-tabs")

    private val tabSpace = new Div(List(tabs))

    private val container = new Div(List(tabSpace))

    def createSubViews = List(container)
}
