package cz.payola.web.client.presenters.graph

import s2js.adapters.browser._
import s2js.adapters.html
import cz.payola.web.client.events._
import cz.payola.web.client.views.graph.PluginSwitchView
import cz.payola.web.client.Presenter
import cz.payola.web.client.views.VertexEventArgs
import cz.payola.web.client.models._
import cz.payola.web.client.views.entity.plugins.DataSourceSelector
import cz.payola.common.entities.settings.OntologyCustomization
import cz.payola.web.client.presenters.entity.settings._
import cz.payola.common.rdf.IdentifiedVertex
import cz.payola.web.client.presenters.entity.PrefixPresenter
import scala.Some

class GraphPresenter(val viewElement: html.Element, prefixApplier: PrefixApplier) extends Presenter
{
    val view = new PluginSwitchView(prefixApplier)

    private var currentOntologyCustomization: Option[OntologyCustomization] = None

    protected val prefixPresenter = new PrefixPresenter()

    def initialize() {
        // Load prefixes first
        prefixPresenter.initialize()

        Model.ontologyCustomizationsChanged += onOntologyCustomizationsChanged _
        view.vertexBrowsingDataSource += onVertexBrowsingDataSource _
        view.vertexSetMain += onVertexSetMain _
        view.customizationsButton.mouseClicked += onCustomizationsButtonClicked _
        view.ontologyCustomizationSelected += onOntologyCustomizationSelected _
        view.ontologyCustomizationEditClicked += onOntologyCustomizationEditClicked _
        view.ontologyCustomizationCreateClicked += onOntologyCustomizationCreateClicked _
        view.userCustomizationSelected += onOntologyCustomizationSelected _
        view.userCustomizationCreateClicked += onUserCustomizationCreateClicked _
        view.userCustomizationEditClicked += onUserCustomizationEditClicked _

        view.render(viewElement)
    }

    private def onUserCustomizationCreateClicked(e: EventArgs[_]) {
        val creator = new UserCustomizationCreator()
        creator.userCustomizationCreated += { e =>
            onOntologyCustomizationSelected(e.asInstanceOf[EventArgs[OntologyCustomization]])
            editUserCustomization(e.target)
        }
        creator.initialize()
    }

    private def onUserCustomizationEditClicked(e: EventArgs[OntologyCustomization]) {
        editUserCustomization(e.target)
        onOntologyCustomizationSelected(e)
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

    private def onCustomizationsButtonClicked(e: EventArgs[_]): Boolean = {
        blockPage("Fetching accessible customizations...")
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
        onOntologyCustomizationSelected(e)
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

    def onVertexSetMain(e: VertexEventArgs[_]) {
        view.setMainVertex(e.vertex)
    }

    private def editOntologyCustomization(customization: OntologyCustomization) {
        val editor = new OntologyCustomizationEditor(customization)
        if (currentOntologyCustomization.exists(_ == customization)) {
            editor.customizationValueChanged += { e => view.updateOntologyCustomization(Some(customization)) }
        }
        editor.initialize()
    }

    private def editUserCustomization(customization: OntologyCustomization) {
        val editor = new UserCustomizationEditor(view.getCurrentGraph, customization, forceUpdateOntologyCustomizations)
        editor.customizationChanged += { e =>
            view.updateOntologyCustomization(Some(e.target.target))
        }

        editor.initialize()
    }

    private def forceUpdateOntologyCustomizations() {
        blockPage("Updating accessible customizations...")
        Model.forceOntologyCustomizationsByOwnershipUpdate { o =>
            view.updateOntologyCustomizations(o)
            unblockPage()
        }(fatalErrorHandler(_))
    }
}
