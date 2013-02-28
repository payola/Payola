package cz.payola.web.client.presenters.entity.analysis

import s2js.adapters.html
import cz.payola.web.client.Presenter
import cz.payola.common.entities.Analysis
import cz.payola.common.entities.Plugin
import cz.payola.web.client.models.Model
import cz.payola.web.client.views.entity.analysis.EditorView

/**
 *
 */
class Editor(val viewElement: html.Element, analysisId: String) extends Presenter
{
    def initialize() {
        blockPage("Loading analysis data...")
        loadAnalysisById(analysisId)(initializeView)
    }

    private def initializeView(analysis: Analysis) {
        loadPlugins({
            plugins =>
                val view = new EditorView(analysis)
                view.render(viewElement)
                bindEvents(view)
                unblockPage()
        })(fatalErrorHandler)
    }

    private def loadAnalysisById(analysisId: String)(successCallback: Analysis => Unit) {
        Model.getOwnedAnalysisById(analysisId)(successCallback)(fatalErrorHandler)
    }

    private def loadPlugins(successCallback: Seq[Plugin] => Unit)(errorHandler: Throwable => Unit) {
        Model.accessiblePlugins(successCallback)(errorHandler)
    }

    private def bindEvents(view: EditorView) {
        view.toolbarView.addDataSource.mouseClicked
        view.toolbarView.addPlugin.mouseClicked
        view.toolbarView.addFork.mouseClicked
        view.toolbarView.addJoin.mouseClicked

        //bind parameters update
        //bind rename
    }
}
