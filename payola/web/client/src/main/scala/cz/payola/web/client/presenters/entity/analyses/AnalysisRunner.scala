package cz.payola.web.client.presenters.entity.analyses

import s2js.adapters.browser._
import cz.payola.web.client._
import cz.payola.web.client.views.entity.analysis.AnalysisRunnerView
import cz.payola.web.shared._
import cz.payola.web.client.presenters.components.EvaluationSuccessEventArgs
import cz.payola.web.client.events._
import cz.payola.common.entities.Analysis
import cz.payola.web.client.presenters.graph.GraphPresenter
import cz.payola.web.client.views.graph.DownloadButtonView
import cz.payola.web.client.views.bootstrap.modals.AlertModal
import cz.payola.common._
import scala.Some
import cz.payola.common.EvaluationInProgress
import cz.payola.common.EvaluationError
import cz.payola.common.EvaluationSuccess

class AnalysisRunner(elementToDrawIn: String, analysisId: String) extends Presenter
{
    val parentElement = document.getElementById(elementToDrawIn)

    val analysisEvaluationSuccess = new UnitEvent[Analysis, EvaluationSuccessEventArgs]

    var analysisRunning = false

    var analysisDone = false

    var graphPresenter: GraphPresenter = null

    var successEventHandler: (EvaluationSuccessEventArgs => Unit) = null

    var evaluationId = ""

    var intervalHandler: Option[Int] = None

    def initialize() {
        blockPage("Loading analysis data...")
        DomainData.getAnalysisById(analysisId) {
            analysis =>
                initUI(analysis)
                unblockPage()
        } {
            err => fatalErrorHandler(err)
        }
    }

    def initUI(analysis: Analysis, timeout: Int = 30): AnalysisRunnerView = {
        val view = new AnalysisRunnerView(analysis, timeout)
        view.render(parentElement)
        view.tabs.hideTab(1)

        view.overviewView.controls.timeoutControl.field.value = timeout

        successEventHandler = getSuccessEventHandler(analysis, view)
        analysisEvaluationSuccess += successEventHandler

        view.overviewView.controls.runBtn.mouseClicked += {
            evt => runButtonClickHandler(view, analysis)
        }

        view
    }

    def getSuccessEventHandler(analysis: Analysis, view: AnalysisRunnerView) : (EvaluationSuccessEventArgs => Unit) = {
        evt: EvaluationSuccessEventArgs =>
            blockPage("Loading result...")

            analysisDone = true
            analysisRunning = false
            intervalHandler.foreach(window.clearInterval(_))
            view.overviewView.controls.stopButton.setIsEnabled(false)
            view.overviewView.controls.timeoutControl.controlGroup.removeCssClass("none")
            view.overviewView.controls.timeoutInfoBar.addCssClass("none")
            view.overviewView.controls.progressBar.setStyleToSuccess()

            graphPresenter = new GraphPresenter(view.resultsView.htmlElement)
            graphPresenter.initialize()

            val downloadButtonView = new DownloadButtonView()
            downloadButtonView.render(graphPresenter.view.toolbar.htmlElement)

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

            graphPresenter.view.updateGraph(Some(evt.graph))

            view.tabs.showTab(1)
            view.tabs.switchTab(1)

            analysisEvaluationSuccess -= successEventHandler

            unblockPage()
    }

    def runButtonClickHandler(view: AnalysisRunnerView, analysis: Analysis) = {
        if (!analysisRunning) {
            blockPage("Starting analysis...")

            uiAdaptAnalysisRunning(view, initUI _, analysis)
            var timeout = view.overviewView.controls.timeoutControl.field.value
            view.overviewView.controls.timeoutInfo.text = timeout.toString

            analysisRunning = true
            AnalysisRunner.runAnalysisById(analysisId, timeout, evaluationId) { id =>
                unblockPage()

                intervalHandler = Some(window.setInterval(() => {
                    view.overviewView.controls.timeoutInfo.text = timeout.toString
                    timeout -= 1
                }, 1000))

                evaluationId = id
                view.overviewView.controls.progressBar.setProgress(0.02)
                schedulePolling(view, analysis)
            } {
                error => fatalErrorHandler(error)
            }

            window.onunload = { _ =>
                onStopClick(view, initUI, analysis)
            }
        }
        false
    }

    private def uiAdaptAnalysisRunning(view: AnalysisRunnerView, initUI: (Analysis, Int) => Unit, analysis: Analysis) {
        view.overviewView.controls.runBtn.setIsEnabled(false)
        view.overviewView.controls.runBtnCaption.text = "Running Analysis..."
        view.overviewView.controls.stopButton.setIsEnabled(true)
        view.overviewView.controls.timeoutControl.controlGroup.addCssClass("none")
        view.overviewView.controls.timeoutInfoBar.removeCssClass("none")
        view.overviewView.controls.stopButton.mouseClicked += { e =>
            onStopClick(view, initUI, analysis)
            false
        }
    }

    private def onStopClick(view: AnalysisRunnerView, initUI: (Analysis, Int) => Unit, analysis: Analysis) {
        if (!analysisDone) {
            analysisRunning = false
            analysisDone = false
            intervalHandler.foreach(window.clearInterval(_))
            val timeout = view.overviewView.controls.timeoutControl.field.value
            view.destroy()
            initUI(analysis, timeout)
            window.onunload = null
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
                "/analysis/%s/evaluation/%s/download.%s".format(analysisId, getAnalysisEvaluationID.get, extension))
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
        view.overviewView.controls.progressBar.setStyleToFailure()
        view.overviewView.controls.progressBar.setActive(false)
        view.overviewView.controls.progressBar.setProgress(1.0)
        analysisDone = true
        view.overviewView.controls.stopButton.setIsEnabled(false)
        intervalHandler.foreach(window.clearInterval(_))

        error.instanceErrors.foreach { err =>
            view.overviewView.analysisVisualizer.setInstanceError(err._1.id, err._2)
        }

        AlertModal.display("Analysis evaluation error",error.error)

        initReRun(view, analysis)
    }

    def evaluationTimeout(view: AnalysisRunnerView, analysis: Analysis) {
        view.overviewView.controls.progressBar.setStyleToFailure()
        view.overviewView.controls.progressBar.setActive(false)
        analysisDone = true
        view.overviewView.controls.stopButton.setIsEnabled(false)
        intervalHandler.foreach(window.clearInterval(_))
        view.overviewView.controls.timeoutControl.controlGroup.removeCssClass("none")
        view.overviewView.controls.timeoutInfoBar.hide()

        AlertModal.display("Time out", "The analysis evaluation has timed out.")

        initReRun(view, analysis)
    }

    def initReRun(view: AnalysisRunnerView, analysis: Analysis) {
        view.overviewView.controls.runBtn.setIsEnabled(true)
        view.overviewView.controls.runBtnCaption.text = "Run Again"
        window.onunload = null
        view.overviewView.controls.runBtn.mouseClicked += { e =>
            view.destroy()

            analysisDone = false
            analysisRunning = false

            val newView = initUI(analysis, view.overviewView.controls.timeoutControl.field.value)
            runButtonClickHandler(newView, analysis)
        }
        successEventHandler = getSuccessEventHandler(analysis, view)
    }

    def evaluationSuccessHandler(success: EvaluationSuccess, analysis: Analysis, view: AnalysisRunnerView) {
        view.overviewView.controls.progressBar.setStyleToSuccess()
        view.overviewView.controls.progressBar.setProgress(1.0)
        analysisDone = true
        view.overviewView.controls.stopButton.setIsEnabled(false)
        intervalHandler.foreach(window.clearInterval(_))

        initReRun(view, analysis)

        window.onunload = null

        view.overviewView.analysisVisualizer.setAllDone()

        success.instanceErrors.foreach {
            err =>
                view.overviewView.analysisVisualizer.setInstanceError(err._1.id, err._2)
        }

        view.overviewView.controls.runBtn.addCssClass("btn-success")
        view.overviewView.controls.progressBar.setActive(false)

        analysisEvaluationSuccess.trigger(new EvaluationSuccessEventArgs(analysis, success.outputGraph))
    }

    def renderEvaluationProgress(progress: EvaluationInProgress, view: AnalysisRunnerView) {
        val progressValue = if (progress.value < 0.02) 0.02 else progress.value
        view.overviewView.controls.progressBar.setProgress(progressValue)

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
