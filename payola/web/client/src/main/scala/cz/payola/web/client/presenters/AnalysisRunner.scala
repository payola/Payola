package cz.payola.web.client.presenters

import s2js.adapters.js.browser.document
import cz.payola.web.client._
import cz.payola.web.client.views.entity.analysis.AnalysisRunnerView
import cz.payola.web.shared._
import s2js.adapters.js.dom
import s2js.adapters.js.browser.window
import cz.payola.web.client.presenters.components.EvaluationEventArgs
import cz.payola.web.client.presenters.notification.Notification
import cz.payola.web.client.events.UnitEvent
import cz.payola.common.entities.Analysis
import cz.payola.web.client.presenters.graph.GraphPresenter
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.bootstrap._
import cz.payola.web.client.views.graph.DownloadButtonView
import cz.payola.web.client.views.bootstrap.modals.AlertModal

class AnalysisRunner(elementToDrawIn: String, analysisId: String) extends Presenter
{
    val parentElement = document.getElementById(elementToDrawIn)

    val analysisEvaluated = new UnitEvent[Analysis, EvaluationEventArgs]

    var analysisRunning = false
    var evaluationId = ""

    def initialize() {
        DomainData.getAnalysisById(analysisId){ analysis =>
            val view = new AnalysisRunnerView(analysis)
            view.render(parentElement)
            view.tabs.hideTab(1)

            analysisEvaluated += {
                evt =>
                    val graphPresenter = new GraphPresenter(view.resultsView.domElement)
                    graphPresenter.initialize()
                    graphPresenter.view.updateGraph(evt.graph)

                    val downloadButtonView = new DownloadButtonView()
                    downloadButtonView.render(graphPresenter.view.toolbar.domElement)

                    downloadButtonView.rdfDownloadAnchor.mouseClicked += { e =>
                        downloadResultAsRDF()
                        true
                    }

                    downloadButtonView.ttlDownloadAnchor.mouseClicked += { e =>
                        downloadResultAsTTL()
                        true
                    }

                    view.tabs.showTab(1)
                    view.tabs.switchTab(1)
                    false
            }

            view.overviewView.controls.runBtn.mouseClicked += { evt =>
                if (!analysisRunning)
                {
                    view.overviewView.controls.runBtn.addCssClass("disabled")
                    analysisRunning = true
                    AnalysisRunner.runAnalysisById(analysisId){id =>
                        evaluationId = id
                        view.overviewView.controls.progressValueBar.setAttribute("style", "width: 2%; height: 40px")
                        schedulePolling(view, analysis)
                    }{error => fatalErrorHandler(error) }
                }
                false
            }

        }{ err => fatalErrorHandler(err) }
    }

    def schedulePolling(view: AnalysisRunnerView, analysis: Analysis) = {
        window.setTimeout(() => { pollingHandler(view, analysis) }, 500)
    }

    private def getAnalysisEvaluationID: Option[String] = {
        val id = evaluationId
        if (id == ""){
            AlertModal.runModal("Evaluation hasn't finished yet.")
            None
        }else{
            Some(id)
        }
    }

    private def downloadResultAs(extension: String){
        if (getAnalysisEvaluationID.isDefined){
            window.open("/analysis/" + analysisId + "/evaluation/" + getAnalysisEvaluationID.get + "/download." + extension)
        }
    }

    private def downloadResultAsRDF(){
        downloadResultAs("xml")
    }

    private def downloadResultAsTTL(){
        downloadResultAs("ttl")
    }
                                                                      /*
    def addClass(el: dom.Element, addedClass: String) = {
        val currentClass = el.getAttribute("class")
        var newClass = currentClass.replaceAllLiterally("alert-warning","")
        newClass = newClass.replaceAllLiterally("alert-error","")
        newClass = newClass.replaceAllLiterally("alert-info","")
        newClass = newClass+" "+addedClass
        el.setAttribute("class",newClass)
    }                                                                   */

    def pollingHandler(view: AnalysisRunnerView, analysis: Analysis) : Unit = {

        AnalysisRunner.getAnalysisProgress(evaluationId) {progress =>
            val percent = (progress.percent*100)

            val display = if (percent > 2){ percent }else{ 2 }

            view.overviewView.controls.progressValueBar.setAttribute("style","width: "+display+"%; height: 40px")
            /*progress.evaluated.map{
                inst => addClass(document.getElementById("inst_"+inst), "alert-warning")
            }
            progress.errors.map{
                inst => addClass(document.getElementById("inst_"+inst), "alert-error")
            }

            progress.evaluated.map{
                inst => addClass(document.getElementById("inst_"+inst), "alert-success")
            } */

            if (!progress.isFinished)
            {
                schedulePolling(view, analysis)
            }else{
                view.markDone(progress.graph)
                analysisEvaluated.trigger(new EvaluationEventArgs(analysis, progress.graph))
                //Notification.postNotification(window.location.href, "Evaluation of analysis "+analysis.name+" is done.")
            }
        }{ error => fatalErrorHandler(error) }
    }
}
