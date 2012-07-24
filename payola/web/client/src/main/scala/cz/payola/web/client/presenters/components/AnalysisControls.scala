package cz.payola.web.client.presenters.components

import cz.payola.web.client.View
import s2js.adapters.js.dom
import s2js.adapters.js.browser.document
import cz.payola.web.shared.AnalysisRunner
import s2js.adapters.js.browser.window
import cz.payola.common.rdf.Graph
import s2js.compiler.javascript
import cz.payola.web.client.views.elements._
import cz.payola.web.client.events.UnitEvent
import cz.payola.web.client.views.elements.Div
import cz.payola.web.client.views.elements.Anchor
import s2js.adapters.js.dom.Element
import cz.payola.web.client.presenters.notification.Notification
import cz.payola.web.client.views.bootstrap.modals.AlertModal

class AnalysisControls(analysisId: String) extends View
{
    val analysisEvaluated = new UnitEvent[AnalysisControls, EvaluationEventArgs]

    val icon = new Italic(List(), "icon-play icon-white")
    val caption = new Text("Run analysis")
    val runBtn = new Anchor(List(icon, caption), "#", "btn btn-primary span2")

    val progressValueBar = new Div(List(),"bar")
    progressValueBar.setAttribute("style", "width: 0%; height: 40px")
    val progressDiv = new Div(List(progressValueBar),"progress progress-striped progress-success active span10")

    var evaluationId = ""

    val wrap = new Div(List(runBtn, progressDiv))

    def render(parent: dom.Element) = {
        wrap.render(parent)
    }

    var analysisRunning = false

    runBtn.mouseClicked += { evt =>
        if (!analysisRunning)
        {
            runBtn.addCssClass("disabled")
            analysisRunning = true
            AnalysisRunner.runAnalysisById(analysisId){id =>
                evaluationId = id
                progressValueBar.setAttribute("style", "width: 2%; height: 40px")
                schedulePolling
            }{error =>
                AlertModal.runModal("Unable to run analysis.")
            }
        }
        false
    }

    def schedulePolling = {
        window.setTimeout(pollingHandler, 500)
    }

    def addClass(el: dom.Element, addedClass: String) = {
        val currentClass = el.getAttribute("class")
        var newClass = currentClass.replaceAllLiterally("alert-warning","")
        newClass = newClass.replaceAllLiterally("alert-error","")
        newClass = newClass.replaceAllLiterally("alert-info","")
        newClass = newClass+" "+addedClass
        el.setAttribute("class",newClass)
    }

    def pollingHandler() : Unit = {

        AnalysisRunner.getAnalysisProgress(evaluationId) {progress =>
            val percent = (progress.percent*100)

            val display = if (percent > 2){ percent }else{ 2 }

            progressValueBar.setAttribute("style","width: "+display+"%; height: 40px")

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
        }{ error =>
            AlertModal.runModal("Unable to determine analysis progress.")
        }
    }

    def markDone(graph: Option[Graph]) = {
        runBtn.addCssClass("btn-success")
        progressDiv.removeCssClass("active")

        analysisEvaluated.trigger(new EvaluationEventArgs(this, graph))
        analysisRunning = false

        Notification.postNotification(window.location.href, "Analysis evalutation done.")
    }

    @javascript("""jQuery("#results-tab-link").click();""")
    def switchTab() = {}

    def domElement : dom.Element = {
        wrap.domElement
    }

    def destroy() {
        // TODO
    }

    def blockDomElement: Element = null // TODO
}
