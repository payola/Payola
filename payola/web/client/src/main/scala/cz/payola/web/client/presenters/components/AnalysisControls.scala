package cz.payola.web.client.presenters.components

import cz.payola.web.client.mvvm.Component
import cz.payola.web.client.mvvm.element._
import s2js.adapters.js.dom.Element
import s2js.adapters.js.browser.document
import cz.payola.web.client.events._
import cz.payola.web.shared.AnalysisRunner
import s2js.adapters.js.browser.window
import cz.payola.common.rdf.Graph

class AnalysisControls(analysisId: String) extends Component
{
    val analysisEvaluated = new ComponentEvent[AnalysisControls, EvaluationEventArgs]

    val icon = new Italic(List(), "icon-play icon-white")
    val caption = new Text("Run analysis")
    val runBtn = new Anchor(List(icon, caption), "#", "btn btn-primary span2")

    val progressValueBar = new Div(List(),"bar")
    progressValueBar.setAttribute("style", "width: 0%")
    val progressDiv = new Div(List(progressValueBar),"progress progress-striped progress-success active span11")

    var evaluationId = ""

    def render(parent: Element = document.body) = {
        runBtn.render(parent)
        progressDiv.render(parent)
    }

    runBtn.clicked += { evt =>
        runBtn.addClass("disabled")
        evaluationId = AnalysisRunner.runAnalysisById(analysisId) //TODO: prevent multiple evaluations
        schedulePolling
        false
    }

    def schedulePolling = {
        window.setTimeout(pollingHandler, 500)
    }

    def addClass(el: Element, addedClass: String) = {
        val currentClass = el.getAttribute("class")
        var newClass = currentClass.replaceAllLiterally("alert-warning","")
        newClass = newClass.replaceAllLiterally("alert-error","")
        newClass = newClass.replaceAllLiterally("alert-info","")
        newClass = newClass+" "+addedClass
        el.setAttribute("class",newClass)
    }

    def pollingHandler() : Unit = {
        val progress = AnalysisRunner.getAnalysisProgress(evaluationId)
        progressValueBar.setAttribute("style","width: "+(progress.percent*100)+"%")

        progress.evaluated.map{
            inst => addClass(document.getElementById("inst_"+inst), "alert-warning")
        }

        progress.errors.map{
            inst => addClass(document.getElementById("inst_"+inst), "alert-error")
        }

        progress.evaluated.map{
            inst => addClass(document.getElementById("inst_"+inst), "alert-success")
        }

        if (!progress.isFinished)
        {
            schedulePolling
        }else{
            markDone(progress.graph)
        }
    }

    def markDone(graph: Option[Graph]) = {
        runBtn.addClass("btn-success")
        progressDiv.removeClass("active")

        analysisEvaluated.trigger(new EvaluationEventArgs(this, graph))
    }
}
