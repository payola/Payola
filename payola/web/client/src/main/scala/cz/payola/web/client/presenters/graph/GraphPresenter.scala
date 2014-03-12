package cz.payola.web.client.presenters.graph

import s2js.adapters.browser._
import s2js.adapters.html
import cz.payola.web.client.events._
import cz.payola.web.client.views.graph.PluginSwitchView
import cz.payola.web.client.Presenter
import cz.payola.web.client.views.VertexEventArgs
import cz.payola.web.client.models._
import cz.payola.web.client.views.entity.plugins.DataSourceSelector
import cz.payola.common.entities.settings._
import cz.payola.web.client.presenters.entity.settings._
import cz.payola.common.rdf.IdentifiedVertex
import cz.payola.web.client.presenters.entity.PrefixPresenter

class GraphPresenter(val viewElement: html.Element, prefixApplier: PrefixApplier, startEvaluationId: Option[String] = None, analysisId: Option[String] = None) extends Presenter
{
    val view = new PluginSwitchView(prefixApplier, startEvaluationId, analysisId)

    private var currentCustomization: Option[DefinedCustomization] = None

    protected val prefixPresenter = new PrefixPresenter()

    def initialize() {
        // Load prefixes first
        prefixPresenter.initialize()

        Model.customizationsChanged += onCustomizationsChanged _
        view.vertexBrowsingDataSource += onVertexBrowsingDataSource _
        view.vertexSetMain += onVertexSetMain _
        view.customizationsButton.mouseClicked += onCustomizationsButtonClicked _
        view.ontologyCustomizationSelected += onCustomizationSelected _
        view.ontologyCustomizationEditClicked += onOntologyCustomizationEditClicked _
        view.ontologyCustomizationCreateClicked += onOntologyCustomizationCreateClicked _
        view.userCustomizationSelected += onCustomizationSelected _
        view.userCustomizationCleared += onCustomizationClear _
        view.userCustomizationCreateClicked += onUserCustomizationCreateClicked _
        view.userCustomizationEditClicked += onUserCustomizationEditClicked _

        view.render(viewElement)
    }

    private def onUserCustomizationCreateClicked(e: EventArgs[_]) {
        val creator = new UserCustomizationCreator()
        creator.userCustomizationCreated += { e =>
            onCustomizationSelected(e.asInstanceOf[EventArgs[OntologyCustomization]])
            editUserCustomization(e.target)
        }
        creator.initialize()
    }

    private def onUserCustomizationEditClicked(e: EventArgs[UserCustomization]) {
        editUserCustomization(e.target)
        onCustomizationSelected(e)
    }

    private def onCustomizationsChanged(e: EventArgs[_]) {
        Model.customizationsByOwnership{ (ontoCustomization, userCustomization) =>
            view.updateAvailableCustomizations(userCustomization, ontoCustomization)
            if (currentCustomization.exists(c => !ontoCustomization.ownedCustomizations.exists(_.contains(c)))
                && currentCustomization.exists(c => !userCustomization.ownedCustomizations.exists(_.contains(c)))) {
                currentCustomization = None
                view.updateCustomization(None)
            }
        }(fatalErrorHandler(_))
    }

    private def onCustomizationsButtonClicked(e: EventArgs[_]): Boolean = {
        blockPage("Fetching accessible customizations...")
        Model.customizationsByOwnership { (ontoCustomization, userCustomization) =>
            view.updateAvailableCustomizations(userCustomization, ontoCustomization)
            unblockPage()
        }(fatalErrorHandler(_))
        true
    }

    private def onOntologyCustomizationCreateClicked(e: EventArgs[_]) {
        val creator = new OntologyCustomizationCreator()
        creator.ontologyCustomizationCreated += { e =>
            onCustomizationSelected(e)
            editOntologyCustomization(e.target)
        }
        creator.initialize()
    }

    private def onOntologyCustomizationEditClicked(e: EventArgs[OntologyCustomization]) {
        editOntologyCustomization(e.target)
        onCustomizationSelected(e)
    }

    private def onCustomizationSelected(e: EventArgs[DefinedCustomization]) {

        currentCustomization = Some(e.target)
        view.updateCustomization(Some(e.target))
    }

    private def onCustomizationClear(e: EventArgs[DefinedCustomization]) {
        currentCustomization = None
        view.updateCustomization(None)
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
        if (currentCustomization.exists(_ == customization)) {
            editor.customizationValueChanged += { e => view.updateCustomization(Some(customization)) }
        }
        editor.initialize()
    }

    private def editUserCustomization(customization: UserCustomization) {
        val editor = new UserCustomizationEditor(view.getCurrentGraphView, customization,
            forceUpdateOntologyCustomizations, prefixApplier)
        editor.customizationChanged += { e =>
            view.updateCustomization(Some(e.target.target))
        }

        editor.initialize()
    }

    private def forceUpdateOntologyCustomizations() {
        blockPage("Updating accessible customizations...")
        Model.forceCustomizationsByOwnershipUpdate { (u, o) =>
            view.updateAvailableCustomizations(u, o)
            unblockPage()
        }(fatalErrorHandler(_))
    }
}
