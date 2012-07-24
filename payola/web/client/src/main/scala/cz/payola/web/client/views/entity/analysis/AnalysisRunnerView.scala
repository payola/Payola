package cz.payola.web.client.views.entity.analysis

import cz.payola.web.client.views.ComposedView
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.bootstrap.Tabs
import cz.payola.common.entities.Analysis
import cz.payola.common.rdf.Graph

class AnalysisRunnerView(analysis: Analysis) extends ComposedView
{
    val overviewView = new AnalysisOverviewView(analysis)
    val resultsView = new Div

    val tabs = new Tabs(List(
        ("Overview", overviewView),
        ("Results", resultsView)
    ),"analysis-tabs")

    private val tabSpace = new Div(List(tabs))
    private val container = new Div(List(tabSpace))

    def createSubViews = List(container)

    def markDone(graph: Option[Graph]){
        overviewView.controls.runBtn.addCssClass("btn-success")
        overviewView.controls.progressDiv.removeCssClass("active")
    }
}
