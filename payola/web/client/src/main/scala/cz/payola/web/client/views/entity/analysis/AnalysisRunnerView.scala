package cz.payola.web.client.views.entity.analysis

import cz.payola.web.client.views.ComposedView
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.bootstrap.Tabs
import cz.payola.common.entities.Analysis

class AnalysisRunnerView(analysis: Analysis, timeoutSeconds: Int) extends ComposedView
{
    val overviewView = new AnalysisOverviewView(analysis, timeoutSeconds)

    val resultsView = new Div

    val tabs = new Tabs(List(
        ("Overview", overviewView),
        ("Results", resultsView)
    ), "analysis-tabs")

    private val tabSpace = new Div(List(tabs))

    private val container = new Div(List(tabSpace))

    def createSubViews = List(container)
}
