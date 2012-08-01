package cz.payola.web.client.presenters.graph

import s2js.adapters.js.dom
import s2js.adapters.js.browser._
import s2js.adapters.js.browser.window
import cz.payola.web.client.events._
import cz.payola.web.client.views.graph.PluginSwitchView
import cz.payola.web.client.Presenter
import cz.payola.web.client.views.VertexEventArgs
import cz.payola.web.client.models.Model
import cz.payola.web.client.views.entity.plugins.DataSourceSelector
import cz.payola.common.entities.settings.OntologyCustomization
import cz.payola.web.client.presenters.entity.settings._

class GraphPresenter(val viewElement: dom.Element) extends Presenter
{
    val view = new PluginSwitchView

    def initialize() {
        Model.ontologyCustomizationsChanged += onOntologyCustomizationsChanged _
        view.vertexBrowsingDataSource += onVertexBrowsingDataSource _
        view.ontologyCustomizationsButton.mouseClicked += onOntologyCustomizationsButtonClicked _
        view.ontologyCustomizationSelected += onOntologyCustomizationSelected _
        view.ontologyCustomizationEditClicked += onOntologyCustomizationEditClicked _
        view.ontologyCustomizationCreateClicked += onOntologyCustomizationCreateClicked _

        view.render(viewElement)
    }

    private def onOntologyCustomizationsChanged(e: EventArgs[_]) {
        Model.ontologyCustomizationsByOwnership(view.updateOntologyCustomizations(_))(fatalErrorHandler(_))
    }

    private def onOntologyCustomizationsButtonClicked(e: BrowserEventArgs[_]): Boolean = {
        blockPage("Fetching accessible ontology customizations.")
        Model.ontologyCustomizationsByOwnership { o =>
            view.updateOntologyCustomizations(o)
            unblockPage()
        }(fatalErrorHandler(_))
        true
    }

    private def onOntologyCustomizationCreateClicked(e: EventArgs[_]) = {
        val creator = new OntologyCustomizationCreator()
        creator.ontologyCustomizationCreated += { e =>
            view.updateOntologyCustomization(Some(e.target))
        }
        creator.initialize()
    }

    private def onOntologyCustomizationEditClicked(e: EventArgs[OntologyCustomization]): Boolean = {
        new OntologyCustomizationEditor(e.target).initialize()
        false
    }

    private def onOntologyCustomizationSelected(e: EventArgs[OntologyCustomization]): Boolean = {
        view.updateOntologyCustomization(Some(e.target))
        false
    }

    private def onVertexBrowsingDataSource(e: VertexEventArgs[_]) {
        blockPage("Fetching accessible data sources.")
        Model.accessibleDataSources { ds =>
            unblockPage()
            val selector = new DataSourceSelector("Browse in different data source: " + e.vertex.uri, ds)
            selector.dataSourceSelected += { d =>
                window.location.href = "/datasource/" + d.target.id + "?uri=" + encodeURI(e.vertex.uri)
            }
            selector.render()
        }(fatalErrorHandler(_))
    }
}
