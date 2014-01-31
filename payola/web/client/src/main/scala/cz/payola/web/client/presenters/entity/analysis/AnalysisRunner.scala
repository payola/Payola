package cz.payola.web.client.presenters.entity.analysis

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
import cz.payola.web.client.views.VertexEventArgs
import cz.payola.web.client.presenters.entity.PrefixPresenter
import s2js.compiler.javascript

/**
 * Presenter responsible for the logic around running an analysis evaluation.
 * @param elementToDrawIn ID of the element to render view into
 * @param analysisId ID of the analysis which will be run
 */
class AnalysisRunner(elementToDrawIn: String, analysisId: String) extends Presenter
{
    var elapsed = 0
    val parentElement = document.getElementById(elementToDrawIn)
    var analysisEvaluationSuccess = new UnitEvent[Analysis, EvaluationSuccessEventArgs]
    var analysisRunning = false
    var analysisDone = false
    var graphPresenter: GraphPresenter = null
    var successEventHandler: (EvaluationSuccessEventArgs => Unit) = null
    var evaluationId = ""
    var storeHandler: Boolean = false
    var intervalHandler: Option[Int] = None
    val prefixPresenter = new PrefixPresenter

    private val pollingPeriod = 500

    @javascript(
        """
          if (window.location.hash){
            var id = window.location.hash.substr(1);
            if (id.length > 0){
                jQuery(".analysis-controls .btn-success").click();
            }
          }
        """)
    def autorun() {}

    @javascript(
        """
          if (window.location.hash){
            var id = window.location.hash.substr(1);
            if (id.length > 0){
                jQuery("#tab-2 .dropdown-menu ."+id+" a").click();
            }
          }else{
            window.location.hash = "cz_payola_web_client_views_graph_table_TripleTablePluginView"
          }
        """)
    def autoswitch() {}

    def initialize() {
        blockPage("Loading analysis data...")
        prefixPresenter.initialize

        DomainData.getAnalysisById(analysisId) {
            analysis =>
                createViewAndInit(analysis)
                unblockPage()
                autorun()
        } {
            err => fatalErrorHandler(err)
        }
    }

    private def createViewAndInit(analysis: Analysis): AnalysisRunnerView = {
        val view = new AnalysisRunnerView(analysis, prefixPresenter.prefixApplier)
        view.render(parentElement)
        view.tabs.hideTab(1)

        successEventHandler = getSuccessEventHandler(analysis, view)
        analysisEvaluationSuccess = new UnitEvent[Analysis, EvaluationSuccessEventArgs]
        analysisEvaluationSuccess += successEventHandler

        view.overviewView.controls.runBtn.mouseClicked += {
            evt => runButtonClickHandler(view, analysis)
        }

        view
    }

    private def getSuccessEventHandler(analysis: Analysis, view: AnalysisRunnerView): (EvaluationSuccessEventArgs => Unit) = {
        evt: EvaluationSuccessEventArgs =>
            blockPage("Loading result...")

            analysisDone = true
            analysisRunning = false
            intervalHandler.foreach(window.clearInterval(_))
            view.overviewView.controls.stopButton.setIsEnabled(false)
            view.overviewView.controls.timeoutInfoBar.addCssClass("none")
            view.overviewView.controls.progressBar.setStyleToSuccess()

            graphPresenter = new GraphPresenter(view.resultsView.htmlElement, prefixPresenter.prefixApplier)
            graphPresenter.initialize()
            graphPresenter.view.vertexBrowsing += onVertexBrowsing

            val downloadButtonView = new DownloadButtonView()
            downloadButtonView.render(graphPresenter.view.toolbar.htmlElement)

            downloadButtonView.rdfDownloadAnchor.mouseClicked += { e =>
                downloadResultAsRDF()
                true
            }

            downloadButtonView.ttlDownloadAnchor.mouseClicked += { e =>
                downloadResultAsTTL()
                true
            }

            graphPresenter.view.setEvaluationId(getAnalysisEvaluationID) //TODO 2/2 this disables cache
            graphPresenter.view.updateGraph(Some(evt.graph), true)

            view.tabs.showTab(1)
            view.tabs.switchTab(1)
            autoswitch()

            analysisEvaluationSuccess -= successEventHandler

            unblockPage()
    }

    private def onVertexBrowsing(e: VertexEventArgs[_]) {
        graphPresenter.onVertexBrowsingDataSource(e)
    }

    private def runButtonClickHandler(view: AnalysisRunnerView, analysis: Analysis) = {
        if (!analysisRunning) {
            analysisRunning = true
            blockPage("Starting analysis...")

            uiAdaptAnalysisRunning(view, createViewAndInit _, analysis)
            view.overviewView.controls.timeoutInfo.text = "0"

            AnalysisRunner.runAnalysisById(analysisId, evaluationId, true) { id =>
                unblockPage()
                elapsed = 0

                intervalHandler = Some(window.setInterval(() => {
                    elapsed += 1
                    view.overviewView.controls.timeoutInfo.text = elapsed.toString
                }, 1000))

                evaluationId = id
                view.overviewView.controls.progressBar.setProgress(0.02)
                schedulePolling(view, analysis)
            } {
                error => fatalErrorHandler(error)
            }

            window.onunload = { _ =>
                onStopClick(view, createViewAndInit, analysis)
            }
        }
        false
    }

    private def uiAdaptAnalysisRunning(view: AnalysisRunnerView, initUI: (Analysis) => Unit, analysis: Analysis) {
        view.overviewView.controls.runBtn.setIsEnabled(false)
        view.overviewView.controls.runBtnCaption.text = "Running Analysis..."
        view.overviewView.controls.stopButton.setIsEnabled(true)
        view.overviewView.controls.timeoutInfoBar.removeCssClass("none")
        view.overviewView.controls.stopButton.mouseClicked += { e =>
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
            window.onunload = null
        }
    }

    private def schedulePolling(view: AnalysisRunnerView, analysis: Analysis) = {
        window.setTimeout(() => {
            pollingHandler(view, analysis)
        }, pollingPeriod)
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
        downloadResultAs("rdf")
    }

    private def downloadResultAsTTL() {
        downloadResultAs("ttl")
    }

    private def pollingHandler(view: AnalysisRunnerView, analysis: Analysis) {
        AnalysisRunner.getEvaluationState(evaluationId, analysis.id, false) {
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

    private def evaluationErrorHandler(error: EvaluationError, view: AnalysisRunnerView, analysis: Analysis) {
        view.overviewView.controls.progressBar.setStyleToFailure()
        view.overviewView.controls.progressBar.setActive(false)
        view.overviewView.controls.progressBar.setProgress(1.0)
        analysisDone = true
        view.overviewView.controls.stopButton.setIsEnabled(false)
        intervalHandler.foreach(window.clearInterval(_))

        error.instanceErrors.foreach { err =>
            view.overviewView.analysisVisualizer.setInstanceError(err._1.id, err._2)
        }

        AlertModal.display("Analysis evaluation error", error.error)

        initReRun(view, analysis)
    }

    private def evaluationTimeout(view: AnalysisRunnerView, analysis: Analysis) {
        view.overviewView.controls.progressBar.setStyleToFailure()
        view.overviewView.controls.progressBar.setActive(false)
        analysisDone = true
        view.overviewView.controls.stopButton.setIsEnabled(false)
        intervalHandler.foreach(window.clearInterval(_))
        view.overviewView.controls.timeoutInfoBar.hide()

        AlertModal.display("Time out", "The analysis evaluation has timed out.")

        initReRun(view, analysis)
    }

    private def initReRun(view: AnalysisRunnerView, analysis: Analysis) {
        view.overviewView.controls.runBtn.setIsEnabled(true)
        view.overviewView.controls.runBtnCaption.text = "Run Again"
        window.onunload = null

        view.overviewView.controls.runBtn.mouseClicked.clear()
        view.overviewView.controls.runBtn.mouseClicked += { e =>
            view.destroy()

            analysisDone = false
            analysisRunning = false

            val newView = createViewAndInit(analysis)
            runButtonClickHandler(newView, analysis)
        }
        successEventHandler = getSuccessEventHandler(analysis, view)
    }

    private def evaluationSuccessHandler(success: EvaluationSuccess, analysis: Analysis, view: AnalysisRunnerView) {
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

    private def renderEvaluationProgress(progress: EvaluationInProgress, view: AnalysisRunnerView) {
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
