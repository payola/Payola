package cz.payola.web.client.views.entity.plugins

import s2js.adapters.html
import s2js.compiler.javascript
import cz.payola.common.entities.Plugin
import cz.payola.web.client.views.elements._
import cz.payola.web.client.View
import cz.payola.web.client.views.elements.Div
import cz.payola.common.entities.plugins._
import cz.payola.web.client.events.SimpleUnitEvent
import cz.payola.web.client.models.PrefixApplier

abstract class PluginInstanceView(
    val pluginInstance: PluginInstance,
    var predecessors: Seq[PluginInstanceView] = Nil,
    prefixApplier: PrefixApplier = new PrefixApplier())
    extends View
{
    private val heading = getHeading

    val parameterNameClicked = new SimpleUnitEvent[ParameterValue[_]]

    private val paramsDiv = new Div(getParameterViews, "parameters")

    private val controlViews = getAdditionalControlsViews

    private val additionalControls = new Div(controlViews, "controls")

    protected val paramsWrapper = new Div(List(paramsDiv),"params-wrapper")

    protected val panelHeading = new Div(heading, "panel-heading")
    protected val panelBody = new Div(List(paramsWrapper, additionalControls),"panel-body")
    protected val alertDiv = new Div(List(panelHeading, panelBody), "panel panel-info instance")

    private val clearSpan = new Span(List(), "clear")

    private val successors = new Div(List(clearSpan, alertDiv), "successors")

    def getHeading: Seq[View] = List(new Text(pluginInstance.plugin.name))

    def getParameterViews: Seq[View]

    def getAdditionalControlsViews: Seq[View]

    def getPlugin: Plugin = pluginInstance.plugin

    def getId: String = pluginInstance.id

    private var parentElement: Option[html.Element] = None

    def render(parent: html.Element) {
        this.parentElement = Some(parent)
        successors.render(parent)

        if (predecessors.nonEmpty) {
            parent.insertBefore(successors.htmlElement, predecessors(0).htmlElement)
        }

        var w = 0.0
        predecessors.foreach {p =>
            successors.htmlElement.insertBefore(p.htmlElement, clearSpan.htmlElement)

            if (w > 0) {
                w += 10
            }
            w += p.htmlElement.offsetWidth
            val pos = w - (p.htmlElement.offsetWidth / 2) - 4

            val conn = new Div(Nil, "connector")
            conn.setAttribute("style", "left: %dpx".format(pos))
            conn.render(successors.htmlElement)

            val plumb = new Div(Nil, "plumb")
            plumb.setAttribute("style", "left: %dpx".format(pos - 3))
            plumb.render(alertDiv.htmlElement)
        }
    }

    override def destroy() {
        if (parentElement.isDefined) {
            predecessors.map {
                p =>
                    parentElement.get.insertBefore(p.htmlElement, htmlElement)
            }
            parentElement.get.removeChild(htmlElement)
        }
    }

    def htmlElement: html.Element = {
        successors.htmlElement
    }

    def getPluginElement: html.Element = {
        alertDiv.htmlElement
    }

    def showControls() {
        additionalControls.removeCssClass("hidden-element")
    }

    def hideControls() {
        additionalControls.addCssClass("hidden-element")
    }

    def setRunning() {
        clearStyle()
        alertDiv.addCssClass("panel-warning")
    }

    def setEvaluated() {
        clearStyle()
        alertDiv.addCssClass("panel-success")
    }

    var hasError = false

    def setError(message: String) {
        if (!hasError) {
            clearStyle()
            alertDiv.addCssClass("panel-danger")
            hasError = true
            alertDiv.setAttribute("rel", "popover")
            alertDiv.setAttribute("data-content", message)
            alertDiv.setAttribute("data-original-title", "Error details")
            activatePopover(alertDiv.htmlElement)
        }
    }

    @javascript("""jQuery(e).popover()""")
    def activatePopover(e: html.Element) {}

    def clearStyle() {
        alertDiv.removeCssClass("panel-warning")
        alertDiv.removeCssClass("panel-success")
        alertDiv.removeCssClass("panel-info")
        alertDiv.removeCssClass("panel-danger")
    }

    def blockHtmlElement: html.Element = successors.htmlElement

    def addCssClass(cssClass: String) {
        successors.addCssClass(cssClass)
    }

    def removeCssClass(cssClass: String) {
        successors.removeCssClass(cssClass)
    }

    def parameterName(param: Parameter[_]): String = param.name

    def filterParams(parameters: Seq[Parameter[_]]): Seq[Parameter[_]] = parameters
}
