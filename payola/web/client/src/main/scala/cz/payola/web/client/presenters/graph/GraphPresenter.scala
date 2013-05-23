package cz.payola.web.client.presenters.graph

import s2js.adapters.browser._
import s2js.adapters.html
import cz.payola.web.client.events._
import cz.payola.web.client.views.graph.PluginSwitchView
import cz.payola.web.client.Presenter
import cz.payola.web.client.views.VertexEventArgs
import cz.payola.web.client.models.Model
import cz.payola.web.client.views.entity.plugins.DataSourceSelector
import cz.payola.common.entities.settings.OntologyCustomization
import cz.payola.web.client.presenters.entity.settings._
import cz.payola.common.rdf.IdentifiedVertex
import cz.payola.web.client.presenters.entity.PrefixPresenter

class GraphPresenter(val viewElement: html.Element) extends Presenter
{
    val view = new PluginSwitchView

    private var currentOntologyCustomization: Option[OntologyCustomization] = None

    private val prefixPresenter = new PrefixPresenter()

    def initialize() {
        // Load prefixes first
        prefixPresenter.initialize()

        Model.ontologyCustomizationsChanged += onOntologyCustomizationsChanged _
        view.vertexBrowsingDataSource += onVertexBrowsingDataSource _
        view.ontologyCustomizationsButton.mouseClicked += onOntologyCustomizationsButtonClicked _
        view.ontologyCustomizationSelected += onOntologyCustomizationSelected _
        view.ontologyCustomizationEditClicked += onOntologyCustomizationEditClicked _
        view.ontologyCustomizationCreateClicked += onOntologyCustomizationCreateClicked _

        view.render(viewElement)
    }

    private def onOntologyCustomizationsChanged(e: EventArgs[_]) {
        Model.ontologyCustomizationsByOwnership { o =>
            view.updateOntologyCustomizations(o)
            if (currentOntologyCustomization.exists(c => !o.ownedCustomizations.exists(_.contains(c)))) {
                currentOntologyCustomization = None
                view.updateOntologyCustomization(None)
            }
        }(fatalErrorHandler(_))
    }

    private def onOntologyCustomizationsButtonClicked(e: EventArgs[_]): Boolean = {
        blockPage("Fetching accessible ontology customizations...")
        Model.ontologyCustomizationsByOwnership { o =>
            view.updateOntologyCustomizations(o)
            unblockPage()
        }(fatalErrorHandler(_))
        true
    }

    private def onOntologyCustomizationCreateClicked(e: EventArgs[_]) {
        val creator = new OntologyCustomizationCreator()
        creator.ontologyCustomizationCreated += { e =>
            onOntologyCustomizationSelected(e)
            editOntologyCustomization(e.target)
        }
        creator.initialize()
    }

    private def onOntologyCustomizationEditClicked(e: EventArgs[OntologyCustomization]) {
        editOntologyCustomization(e.target)
    }

    private def onOntologyCustomizationSelected(e: EventArgs[OntologyCustomization]) {
        currentOntologyCustomization = Some(e.target)
        view.updateOntologyCustomization(Some(e.target))
    }

    def onVertexBrowsingDataSource(e: VertexEventArgs[_]) {

        e.vertex match {
            case i: IdentifiedVertex => {
                blockPage("Fetching accessible data sources...")
                Model.accessibleDataSources { ds =>
                    unblockPage()
                    val selector = new DataSourceSelector("Browse in different data source: " + i.uri, ds)
                    selector.dataSourceSelected += { d =>
                        window.location.href = "/datasource/%s?uri=%s".format(d.target.id,
                                                                            s2js.adapters.js.encodeURIComponent(i.uri))
                    }
                    selector.render()
                }(fatalErrorHandler(_))
            }
        }
    }

    private def editOntologyCustomization(customization: OntologyCustomization) {
        val editor = new OntologyCustomizationEditor(customization)
        if (currentOntologyCustomization.exists(_ == customization)) {
            editor.customizationValueChanged += { e => view.updateOntologyCustomization(Some(customization)) }
        }
        editor.initialize()
    }
}
