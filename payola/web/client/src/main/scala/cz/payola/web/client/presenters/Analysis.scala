package cz.payola.web.client.presenters

import cz.payola.web.client.presenters.components.AnalysisControls
import cz.payola.web.client.views.graph.PluginSwitchView
import cz.payola.web.client.Presenter
import cz.payola.web.client.views.bootstrap._
import cz.payola.web.client.views.elements._
import s2js.adapters.js.browser._
import cz.payola.web.client.views.bootstrap.modals.AlertModal

class Analysis(elementToDrawIn: String, analysisId: String) extends Presenter
{
    val controls = new AnalysisControls(analysisId)

    private val graphView = new PluginSwitchView

    private def getAnalysisEvaluationID: Option[String] = {
        val id = controls.evaluationId
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

    def initialize() {
        controls.render(document.getElementById("analysis-controls"))
        graphView.render(document.getElementById(elementToDrawIn))

        val graphToolbar = graphView.toolbar
        val rdfDownloadAnchor = new Anchor(List(new Text("Download As RDF")))
        rdfDownloadAnchor.mouseClicked += { e =>
            downloadResultAsRDF()
            true
        }

        val ttlDownloadAnchor = new Anchor(List(new Text(" Download As TTL")))
        ttlDownloadAnchor.mouseClicked += { e =>
            downloadResultAsTTL()
            true
        }

        val downloadButton = new DropDownButton(List(
            new Icon(Icon.download),
            new Text(" Download  ")
        ),
            List(
                new ListItem(List(rdfDownloadAnchor)),
                new ListItem(List(ttlDownloadAnchor))
            )
        )
        downloadButton.render(graphToolbar.domElement)
    }

    controls.analysisEvaluated += {
        evt =>
            graphView.updateGraph(evt.graph)
            controls.switchTab
            false
    }
}
