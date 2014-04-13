package cz.payola.web.client.presenters.entity.plugins

import scala.collection.mutable
import s2js.adapters.html
import cz.payola.web.shared.managers._
import cz.payola.web.client.views.VertexEventArgs
import cz.payola.web.client.Presenter
import cz.payola.web.client.presenters.graph.GraphPresenter
import cz.payola.web.client.views.entity.plugins._
import cz.payola.common.ValidationException
import cz.payola.web.client.views.bootstrap.modals.AlertModal
import cz.payola.web.client.events._
import cz.payola.common.rdf._
import s2js.adapters.browser.window
import s2js.compiler.javascript
import cz.payola.web.client.presenters.entity.PrefixPresenter
import cz.payola.web.client.util.UriHashTools

class DataSourceBrowser(
    val viewElement: html.Element,
    val dataSourceId: String,
    val dataSourceName: String,
    val initialVertexUri: String = "")
    extends Presenter
{
    private val view = new DataSourceBrowserView(dataSourceName)

    private var graphPresenter: GraphPresenter = null

    private val prefixPresenter = new PrefixPresenter()

    private var languagesLoaded = false

    private var allowInitialGraphHistoryReload = false

    def initialize() {
        // First init prefixes
        prefixPresenter.initialize()
        initHistory()

        graphPresenter = new GraphPresenter(view.graphViewSpace.htmlElement, prefixPresenter.prefixApplier, None, None)

        // Initialize the sub presenters.
        graphPresenter.initialize()

        // Register the event handlers.
        view.goButton.mouseClicked += onGoButtonClicked _
        view.sparqlQueryButton.mouseClicked += onSparqlQueryButtonClicked _
        view.nodeUriInput.keyPressed += onNodeUriKeyPressed _
        graphPresenter.view.languagesButton.mouseClicked += onLanguagesButtonClicked _
        graphPresenter.view.vertexBrowsing += onVertexBrowsing _

        view.render(viewElement)

        if (UriHashTools.getUriParameter("browseUri") == 0) {
            // If the default URI isn't specified, display the initial graph.
            if (initialVertexUri == "") {
                allowInitialGraphHistoryReload = false
                loadInitialGraph()
            } else {
                addToHistoryAndGo(initialVertexUri, false)
            }
        } else {
            addToHistoryAndGo(UriHashTools.decodeURIComponent(UriHashTools.getUriParameter("browseUri")), true)
        }
    }

    private def loadInitialGraph() {
        blockPage("Fetching the initial graph...")
        DataSourceManager.getInitialGraphFirstTripleUri(dataSourceId) { firstUri =>
            graphPresenter.view.setBrowsingURI(firstUri)
            DataSourceManager.getInitialGraph(dataSourceId) { graph =>
                graphPresenter.view.updateGraph(graph, true)
                unblockPage()
                if(UriHashTools.isParameterSet(UriHashTools.customizationParameter))
                    graphPresenter.onViewPluginChanged(null)
            }(fatalErrorHandler(_))

        }(fatalErrorHandler(_))
    }

    private def onLanguagesButtonClicked(e: EventArgs[_]): Boolean = {
        if(!languagesLoaded){
            blockPage("Fetching available languages...")
            DataSourceManager.getLanguages(dataSourceId) { o =>
                o.foreach(res => graphPresenter.view.updateLanguages(res))
                languagesLoaded = true
                unblockPage()
            } (fatalErrorHandler(_))
        }
        true
    }

    private def onVertexBrowsing(e: VertexEventArgs[_]) {
        e.vertex match {
            case i: IdentifiedVertex => addToHistoryAndGo(i.uri, false)
        }
    }

    private def onGoButtonClicked(e: EventArgs[_]): Boolean = {
        addToHistoryAndGo(view.nodeUriInput.value, false)
        false
    }

    private def onSparqlQueryButtonClicked(e: EventArgs[_]): Boolean = {
        val modal = new SparqlQueryModal
        graphPresenter.view.setBrowsingURI(None)
        modal.confirming += { _ =>
            modal.block("Executing the SPARQL query.")
            DataSourceManager.executeSparqlQuery(dataSourceId, modal.sparqlQueryInput.value) { g =>
                modal.unblock()
                modal.destroy()

                graphPresenter.view.clear()
                graphPresenter.view.updateGraph(g, true)
            } { error =>
                modal.unblock()
                error match {
                    case v: ValidationException => AlertModal.display("Error", v.message)
                    case t => fatalErrorHandler(t)
                }
            }
            false
        }
        modal.render()
        false
    }

    private def onNodeUriKeyPressed(e: KeyboardEventArgs[_]): Boolean = {
        if (e.keyCode == 13) {
            onGoButtonClicked(e)
        } else {
            true
        }
    }

    private def addToHistoryAndGo(prefixedUri: String, forceUpdate: Boolean) {

        val uri = prefixPresenter.prefixApplier.disapplyPrefix(prefixedUri)
        UriHashTools.setUriParameter("browseUri", UriHashTools.encodeURIComponent(uri))
        allowInitialGraphHistoryReload = true

        //binded JQuery action calls updateView after hash in URI is changed
        //if browsing Uri is set on datastourceBrowser initialization, that the jquery binding does not fire any event
        if(forceUpdate)
            updateView(true, uri)
    }

    /**
     * Is called via event binded to URI hash parameter change (@see initHistory())
     */
    private def updateView(clearGraph: Boolean, uri: String) {
        view.nodeUriInput.value = prefixPresenter.prefixApplier.applyPrefix(uri)
        view.nodeUriInput.setIsEnabled(false)
        graphPresenter.view.setBrowsingURI(Some(uri))

        blockPage("Fetching the node neighbourhood...")
        DataSourceManager.getNeighbourhood(dataSourceId, uri) { graph =>
            if (clearGraph) {
                graphPresenter.view.clear()
            }
            graphPresenter.view.updateGraph(graph, true)
            graphPresenter.view.drawGraph()

            view.nodeUriInput.setIsEnabled(true)
            unblockPage()
        }(fatalErrorHandler(_))
    }

    @javascript("""
                  $(window).bind( 'hashchange', function(e) {
                        var uri = cz.payola.web.client.util.UriHashTools.get().getUriParameter("browseUri");
                        uri = cz.payola.web.client.util.UriHashTools.get().decodeURIComponent(uri);
                        if(uri != "") {
                            self.updateView(true, uri);
                        } else if(self.allowInitialGraphHistoryReload) {
                            self.view.nodeUriInput.updateValue("");
                            self.allowInitialGraphHistoryReload = false;
                            self.loadInitialGraph();
                        }
                  });
                """)
    private def initHistory(){}
}
