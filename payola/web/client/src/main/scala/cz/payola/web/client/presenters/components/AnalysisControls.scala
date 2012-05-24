package cz.payola.web.client.presenters.components

import cz.payola.web.client.mvvm_api.Component
import cz.payola.web.client.mvvm_api.element._
import s2js.adapters.js.dom.Element
import s2js.adapters.js.browser.document
import cz.payola.web.client.events._
import cz.payola.web.shared.AnalysisRunner
import s2js.adapters.js.browser.window


class AnalysisControls(analysisId: String) extends Component
{
    val analysisTriggered = new ComponentEvent[AnalysisControls, ClickedEventArgs[AnalysisControls]]

    val icon = new I(List(), "icon-play icon-white")
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

    a.clicked += { evt =>
        a.addClass("disabled")
        evaluationId = AnalysisRunner.runAnalysisById(analysisId) //TODO: prevent multiple evaluations
        schedulePolling
        false
    }

    def schedulePolling = {
        window.setTimeout(pollingHandler, 500)
    }

    def pollingHandler = {
        val progress = AnalysisRunner.getAnalysisProgress(evaluationId)
        progressValueBar.setAttribute("style","width: "+progress.percent+"%")

        progress.evaluated.map{
            inst => document.getElementById("inst_".inst).setAttribute("class","alert-success")
        }

        progress.error.map{
            inst => document.getElementById("inst_".inst).setAttribute("class","alert-error")
        }

        progress.evaluated.map{
            inst => document.getElementById("inst_".inst).setAttribute("class","alert-warning")
        }

        if (!progress.isFinished)
        {
            schedulePolling
        }else{
            markDone
        }
    }

    def markDone = {
        runBtn.addClass("btn-success")
        //TODO: progressbar should be deactivated
    }
}
