package cz.payola.web.client.views.graph.table

import s2js.adapters.browser._
import s2js.adapters.html
import cz.payola.common.rdf._
import cz.payola.web.client.View
import cz.payola.web.client.views.VertexEventArgs
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.bootstrap.Icon
import cz.payola.web.client.views.graph.PluginView
import cz.payola.web.client.models.PrefixApplier
import form.fields.TextInput
import cz.payola.web.client.views.bootstrap.modals.FatalErrorModal
import cz.payola.web.shared.transformators.TripleTableTransformator

abstract class TablePluginView(name: String, prefixApplier: Option[PrefixApplier])
    extends PluginView(name, prefixApplier)
{
    protected val tablePluginWrapper = new Div()
    protected val tableWrapper = new Div().setAttribute("style", "padding: 0 20px; min-height: 200px; margin:0 auto;")

    protected val allowedLinesOnPage = 50
    protected var currentPage = 0
    private var pagesCount = 0

    def createSubViews = List(tablePluginWrapper)

    override def updateGraph(graph: Option[Graph], contractLiterals: Boolean = true) {
        updateGraphPage(graph, contractLiterals)
    }

    def updateGraphPage(graph: Option[Graph], contractLiterals: Boolean = true, page: Int = 0) {
        if (graph != currentGraph) {
            currentPage = page
            // Remove the old table.
            tableWrapper.removeAllChildNodes()
            tablePluginWrapper.removeAllChildNodes()

            if (graph.isEmpty) {
                renderMessage(tablePluginWrapper.htmlElement, "The graph is empty...")
            } else {
                tablePluginWrapper.htmlElement.appendChild(tableWrapper.htmlElement)

                renderTablePage(graph, 0)
                if(pagesCount != 0)
                    createListingTools().render(tablePluginWrapper.htmlElement)
            }
        }

        super.updateGraph(graph, true)
    }

    private def renderTablePage(graph: Option[Graph], pageNumber: Int) {
        tableWrapper.removeAllChildNodes()

        val table = document.createElement[html.Element]("table")
        table.className = "table table-striped table-bordered table-condensed"

        tableWrapper.htmlElement.appendChild(table)
        pagesCount = fillTable(graph, addElement(table, "thead"), addElement(table, "tbody"), pageNumber)
    }

    protected def createListingTools(): View = {
        val info = new Text("Page "+(currentPage + 1)+" of "+pagesCount)

        val firstPage = new Button(new Text("First"), "", new Icon(Icon.fast_backward))
        firstPage.mouseClicked += { e =>
            if (currentPage > 0) {
                if(evaluationId.isDefined) {
                    paginateToPage(0)
                } else {
                    currentPage = 0
                    renderTablePage(currentGraph, currentPage)
                    info.text = "Page "+(currentPage + 1)+" of "+pagesCount
                }
            }
            false
        }

        val previousPage = new Button(new Text("Previous"), "", new Icon(Icon.backward))
        previousPage.mouseClicked += { e =>
            if (currentPage > 0) {
                if(evaluationId.isDefined) {
                    paginateToPage(currentPage - 1)
                } else {
                    currentPage -= 1
                    renderTablePage(currentGraph, currentPage)
                    info.text = "Page "+(currentPage + 1)+" of "+pagesCount
                }
            }
            false
        }

        val nextPage = new Button(new Text("Next"), "", new Icon(Icon.forward))
        nextPage.mouseClicked += { e =>
            if (currentPage < pagesCount - 1) {
                if(evaluationId.isDefined) {
                    paginateToPage(currentPage + 1)
                } else {
                    currentPage += 1
                    renderTablePage(currentGraph, currentPage)
                    info.text = "Page "+(currentPage + 1)+" of "+pagesCount
                }
            }
            false
        }

        val lastPage = new Button(new Text("Last"), "", new Icon(Icon.fast_forward))
        lastPage.mouseClicked += { e =>
            if (currentPage < pagesCount - 1) {
                if(evaluationId.isDefined) {
                    paginateToPage(pagesCount - 1)
                } else {
                    currentPage = pagesCount - 1
                    renderTablePage(currentGraph, currentPage)
                    info.text = "Page "+(currentPage + 1)+" of "+pagesCount
                }
            }
            false
        }

        val jumpTextArea = new TextInput("jump", "")
        val jumpButton = new Button(new Text("Go"), "", new Icon(Icon.play))
        jumpButton.mouseClicked += { e =>
            val jumpToPageNumber = jumpTextArea.value.toInt - 1
            if(currentPage != jumpToPageNumber) {
                val goingToPage = if(jumpToPageNumber >= 0 && jumpToPageNumber <= pagesCount) {
                    jumpToPageNumber } else { currentPage }

                if(evaluationId.isDefined) {
                    paginateToPage(goingToPage)
                } else {
                    currentPage = goingToPage
                    renderTablePage(currentGraph, currentPage)
                    info.text = "Page "+(currentPage + 1)+" of "+pagesCount
                }
            }
            false
        }

        new Div(List(firstPage, previousPage, nextPage, lastPage, info, jumpTextArea, jumpButton)).setAttribute(
            "style", "width:800px; margin: 0 auto;")
    }

    private def paginateToPage(goingToPage: Int) {
        if(evaluationId.isDefined) {
            TripleTableTransformator.getCachedPage(evaluationId.get, goingToPage, allowedLinesOnPage) { paginated =>
                updateGraphPage(Some(paginated), true, goingToPage)
            } { error =>
                val modal = new FatalErrorModal(error.toString())
                modal.render()
            }
        }
    }

    def fillTable(graph: Option[Graph], tableHead: html.Element, tableBody: html.Element, pageNumber: Int): Int

    protected def createVertexView(vertex: IdentifiedVertex): View = {
        val dataSourceAnchor = new Anchor(List(new Icon(Icon.hdd)))
        dataSourceAnchor.mouseClicked += { e =>
            vertexBrowsingDataSource.trigger(new VertexEventArgs[this.type](this, vertex))
            false
        }
        val uri = prefixApplier.map(_.applyPrefix(vertex.uri)).getOrElse(vertex.uri)
        val browsingAnchor = new Anchor(List(new Text(uri)))
        browsingAnchor.mouseClicked += { e =>
            vertexBrowsing.trigger(new VertexEventArgs[this.type](this, vertex))
            false
        }

        new Span(List(dataSourceAnchor, new Span(List(new Text(" "))), browsingAnchor))
    }

    protected def addRow(table: html.Element): html.Element = addElement(table, "tr")

    protected def insertRow(table: html.Element, insertBefore: html.Element): html.Element = insertElement(table, insertBefore, "tr")

    protected def addCell(row: html.Element, isHeader: Boolean = false) = addElement(row, if (isHeader) "th" else "td")

    private def insertElement(parent: html.Element, followingSibling: html.Element, name: String): html.Element = {
        val element = document.createElement[html.Element](name)
        parent.insertBefore(element, followingSibling)
        element
    }

    private def addElement(parent: html.Element, name: String): html.Element = {
        val element = document.createElement[html.Element](name)
        parent.appendChild(element)
        element
    }
}
