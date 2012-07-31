package cz.payola.web.client.presenters

import s2js.adapters.js.browser.document
import cz.payola.web.client._
import cz.payola.web.client.views.entity.analysis.AnalysisRunnerView
import cz.payola.web.shared._
import s2js.adapters.js.browser.window
import cz.payola.web.client.presenters.components.EvaluationSuccessEventArgs
import cz.payola.web.client.events.UnitEvent
import cz.payola.common.entities.Analysis
import cz.payola.web.client.presenters.graph.GraphPresenter
import cz.payola.web.client.views.graph.DownloadButtonView
import cz.payola.web.client.views.bootstrap.modals.AlertModal

class AnalysisRunner(elementToDrawIn: String, analysisId: String) extends Presenter
{
    val parentElement = document.getElementById(elementToDrawIn)

    val analysisEvaluationSuccess = new UnitEvent[Analysis, EvaluationSuccessEventArgs]

    var analysisRunning = false

    var analysisDone = false

    var evaluationId = ""

    var intervalHandler: Option[Int] = None

    def initialize() {
        blockPage("Loading analysis data")
        DomainData.getAnalysisById(analysisId) {
            analysis =>
                initUI(analysis)
                unblockPage()
        } {
            err => fatalErrorHandler(err)
        }
    }

    def initUI(analysis: Analysis) : AnalysisRunnerView = {
        val view = new AnalysisRunnerView(analysis, 30)
        view.render(parentElement)
        view.tabs.hideTab(1)

        analysisEvaluationSuccess += {
            evt =>
                analysisDone = true
                analysisRunning = false
                intervalHandler.foreach(window.clearInterval(_))
                view.overviewView.controls.stopButton.addCssClass("disabled")

                val graphPresenter = new GraphPresenter(view.resultsView.domElement)
                graphPresenter.initialize()
                graphPresenter.view.updateGraph(Some(evt.graph))

                val downloadButtonView = new DownloadButtonView()
                downloadButtonView.render(graphPresenter.view.toolbar.domElement)

                downloadButtonView.rdfDownloadAnchor.mouseClicked += {
                    e =>
                        downloadResultAsRDF()
                        true
                }

                downloadButtonView.ttlDownloadAnchor.mouseClicked += {
                    e =>
                        downloadResultAsTTL()
                        true
                }

                view.tabs.showTab(1)
                view.tabs.switchTab(1)
                false
        }

        view.overviewView.controls.runBtn.mouseClicked += {
            evt => runButtonClickHandler(view, analysis)
        }

        view
    }

    def runButtonClickHandler(view: AnalysisRunnerView, analysis: Analysis) = {
        if (!analysisRunning) {
            uiAdaptAnalysisRunning(view, initUI _, analysis)
            var timeout = view.overviewView.controls.timeoutControl.input.value.toInt

            analysisRunning = true
            AnalysisRunner.runAnalysisById(analysisId, timeout) {
                id =>

                    intervalHandler = Some(window.setInterval(() => {
                        view.overviewView.controls.timeoutInfo.text = timeout.toString
                        timeout -= 1
                    }, 1000))

                    evaluationId = id
                    view.overviewView.controls.progressValueBar.setAttribute("style", "width: 2%; height: 40px")
                    schedulePolling(view, analysis)
            } {
                error => fatalErrorHandler(error)
            }
        }
        false
    }

    private def uiAdaptAnalysisRunning(view: AnalysisRunnerView, initUI: (Analysis) => Unit, analysis: Analysis) {
        view.overviewView.controls.runBtn.addCssClass("disabled")
        view.overviewView.controls.stopButton.removeCssClass("disabled")
        view.overviewView.controls.timeoutControl.controlGroup.addCssClass("none")
        view.overviewView.controls.timeoutInfoBar.removeCssClass("none")
        view.overviewView.controls.stopButton.mouseClicked += {
            e =>
                onStopClick(view, initUI, analysis)
                false
        }
    }

    private def onStopClick(view: AnalysisRunnerView, initUI: (Analysis) => Unit, analysis: Analysis) {
        if (!analysisDone) {
            analysisRunning = false
            analysisDone = false
            intervalHandler.foreach(window.clearInterval(_))
            view.destroy()
            initUI(analysis)
        }
    }

    def schedulePolling(view: AnalysisRunnerView, analysis: Analysis) = {
        window.setTimeout(() => {
            pollingHandler(view, analysis)
        }, 500)
    }

    private def getAnalysisEvaluationID: Option[String] = {
        val id = evaluationId
        if (id == "") {
            None
        } else {
            Some(id)
        }
    }

    private def downloadResultAs(extension: String) {
        if (getAnalysisEvaluationID.isDefined) {
            window.open(
                "/analysis/" + analysisId + "/evaluation/" + getAnalysisEvaluationID.get + "/download." + extension)
        } else {
            AlertModal.display("Evaluation hasn't finished yet.", "")
        }
    }

    private def downloadResultAsRDF() {
        downloadResultAs("xml")
    }

    private def downloadResultAsTTL() {
        downloadResultAs("ttl")
    }

    def pollingHandler(view: AnalysisRunnerView, analysis: Analysis) {
        AnalysisRunner.getEvaluationState(evaluationId) {
            state =>
                state match {
                    case s: EvaluationInProgress => renderEvaluationProgress(s, view)
                    case s: EvaluationError => evaluationErrorHandler(s, view, analysis)
                    case s: EvaluationSuccess => evaluationSuccessHandler(s, analysis, view)
                    case s: EvaluationTimeout => evaluationTimeout(view, analysis)
                }

                if (state.isInstanceOf[EvaluationInProgress]) {
                    schedulePolling(view, analysis)
                }
        } {
            error => fatalErrorHandler(error)
        }
    }

    def evaluationErrorHandler(error: EvaluationError, view: AnalysisRunnerView, analysis: Analysis) {
        view.overviewView.controls.progressDiv.addCssClass("progress-danger")
        view.overviewView.controls.progressDiv.removeCssClass("progress-success")
        view.overviewView.controls.progressDiv.removeCssClass("active")
        view.overviewView.controls.progressValueBar.setAttribute("style", "width:100%; height: 40px")
        analysisDone = true
        view.overviewView.controls.stopButton.addCssClass("disabled")
        intervalHandler.foreach(window.clearInterval(_))

        error.instanceErrors.foreach {
            err =>
                view.overviewView.analysisVisualizer.setInstanceError(err._1.id, err._2)
        }

        initReRun(view, analysis)
    }

    def evaluationTimeout(view: AnalysisRunnerView, analysis: Analysis) {
        view.overviewView.controls.progressDiv.addCssClass("progress-danger")
        view.overviewView.controls.progressDiv.removeCssClass("progress-success")
        view.overviewView.controls.progressDiv.removeCssClass("active")
        analysisDone = true
        view.overviewView.controls.stopButton.addCssClass("disabled")
        intervalHandler.foreach(window.clearInterval(_))

        AlertModal.display("Time out", "The analysis evaluation has timed out.")

        initReRun(view, analysis)
    }

    def initReRun(view: AnalysisRunnerView, analysis: Analysis) {
        view.overviewView.controls.runBtn.removeCssClass("disabled")
        view.overviewView.controls.runBtnCaption.text = "Run again"

        view.overviewView.controls.runBtn.mouseClicked += { e =>
            view.destroy()

            analysisDone = false
            analysisRunning = false

            val newView = initUI(analysis)
            runButtonClickHandler(newView, analysis)
        }
    }

    def evaluationSuccessHandler(success: EvaluationSuccess, analysis: Analysis, view: AnalysisRunnerView) {
        view.overviewView.controls.progressValueBar.addCssClass("progress-danger")
        view.overviewView.controls.progressValueBar.removeCssClass("progress-success")
        analysisDone = true
        view.overviewView.controls.stopButton.addCssClass("disabled")
        intervalHandler.foreach(window.clearInterval(_))

        success.instanceErrors.foreach {
            err =>
                view.overviewView.analysisVisualizer.setInstanceError(err._1.id, err._2)
        }

        view.overviewView.controls.runBtn.addCssClass("btn-success")
        view.overviewView.controls.progressDiv.removeCssClass("active")

        analysisEvaluationSuccess.trigger(new EvaluationSuccessEventArgs(analysis, success.outputGraph))
    }

    def renderEvaluationProgress(progress: EvaluationInProgress, view: AnalysisRunnerView) {
        val percent = (progress.value * 100)
        val display = if (percent > 2) {
            percent
        } else {
            2
        }
        view.overviewView.controls.progressValueBar.setAttribute("style", "width: " + display + "%; height: 40px")

        progress.evaluatedInstances.map {
            inst => view.overviewView.analysisVisualizer.setInstanceEvaluated(inst.id)
        }
        progress.errors.map {
            tuple => view.overviewView.analysisVisualizer.setInstanceError(tuple._1.id, tuple._2)
        }
        progress.runningInstances.map {
            inst => view.overviewView.analysisVisualizer.setInstanceRunning(inst._1.id)
        }
    }
}
