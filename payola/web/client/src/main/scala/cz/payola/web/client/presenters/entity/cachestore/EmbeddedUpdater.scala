package cz.payola.web.client.presenters.entity.cachestore

import cz.payola.web.client.Presenter
import s2js.compiler.javascript
import cz.payola.web.shared.AnalysisRunner
import s2js.adapters.browser._
import cz.payola.common._
import cz.payola.common.EvaluationError

class EmbeddedUpdater(analysisId: String, var evaluationId: String, embeddedHashId: String, updateButtonId: String) extends Presenter
{
    private var updateRunning = false
    private val pollingPeriod = 500


    def initialize() {
        updateEmbeddedResult()
    }

    def updateEmbeddedResult() {
        if (!updateRunning) {
            updateRunning = true

            AnalysisRunner.runAnalysisById(analysisId, evaluationId) { newEvaluationId =>
                schedulePolling(analysisId, newEvaluationId)
            } {
                error => fatalErrorHandler(error)
            }
        }
    }

    private def schedulePolling(analysId: String, newEvaluationId: String) = {
        window.setTimeout(() => {
            pollingHandler(analysId, newEvaluationId)
        }, pollingPeriod)
    }

    private def pollingHandler(analysId: String, newEvaluationId: String) {
        AnalysisRunner.getEvaluationState(newEvaluationId, analysId, embeddedHashId) {
            state =>
                state match {
                    case s: EvaluationError =>
                        updateError(updateButtonId)
                    case s: EvaluationCompleted =>
                        updateEvaluationAnchor(evaluationId, newEvaluationId)
                        updateSuccessful(updateButtonId, evaluationId)
                    case s: EvaluationTimeout =>
                        updateError(updateButtonId)
                    case _ =>
                }

                if (state.isInstanceOf[EvaluationInProgress]) {
                    schedulePolling(analysId, newEvaluationId)
                }
        } {
            error => fatalErrorHandler(error)
        }
    }

    @javascript("""cacheStoreUpdateButtonResetSucc(updateButtonElementId, oldEvaluationId);""")
    private def updateSuccessful(updateButtonElementId: String, oldEvaluationId: String) {}

    @javascript("""
                   var evaluationLinkElement = document.getElementById('result'+oldEvaluationId);
                   var linkHref = evaluationLinkElement.getAttribute('href');
                   linkHref = linkHref.substring(0, linkHref.indexOf('evaluation'))+'evaluation='+newEvaluationId;
                   evaluationLinkElement.setAttribute('href', linkHref);

                   var linkText = evaluationLinkElement.innerHTML;
                   linkText = linkText.substring(0, linkText.indexOf('evaluation'))+'evaluation='+newEvaluationId;
                   evaluationLinkElement.innerHTML = linkText;
                """)
    private def updateEvaluationAnchor(oldEvaluationId: String, newEvaluationId: String) {}

    @javascript("""cacheStoreUpdateButtonResetErr(updateButtonElementId);""")
    private def updateError(updateButtonElementId: String) {}
}
