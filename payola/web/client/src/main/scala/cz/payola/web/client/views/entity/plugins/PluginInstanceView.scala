package cz.payola.web.client.views.entity.plugins

import s2js.adapters.html
import s2js.compiler.javascript
import cz.payola.common.entities.Plugin
import cz.payola.web.client.views.elements._
import cz.payola.web.client.View
import cz.payola.web.client.views.elements.Div
import cz.payola.common.entities.plugins._

abstract class PluginInstanceView(
    val pluginInstance: PluginInstance,
    var predecessors: Seq[PluginInstanceView] = Nil)
    extends View
{
    private val heading = new Heading(List(new Text(pluginInstance.plugin.name)), 3)

    private val paramsDiv = new Div(getParameterViews, "parameters")

    private val controlViews = getAdditionalControlsViews

    private val additionalControls = new Div(controlViews, "controls")

    protected val paramsWrapper = new Div(List(paramsDiv),"params-wrapper")
    protected val alertDiv = new Div(List(heading, paramsWrapper, additionalControls), "alert alert-info instance")

    private val clearSpan = new Span(List(), "clear")

    private val successors = new Div(List(clearSpan, alertDiv), "successors")

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
        alertDiv.addCssClass("alert-warning")
    }

    def setEvaluated() {
        clearStyle()
        alertDiv.addCssClass("alert-success")
    }

    var hasError = false

    def setError(message: String) {
        if (!hasError) {
            clearStyle()
            alertDiv.addCssClass("alert-danger")
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
        alertDiv.removeCssClass("alert-warning")
        alertDiv.removeCssClass("alert-success")
        alertDiv.removeCssClass("alert-info")
        alertDiv.removeCssClass("alert-danger")
    }

    def blockHtmlElement: html.Element = successors.htmlElement

    def addCssClass(cssClass: String) {
        successors.addCssClass(cssClass)
    }

    def removeCssClass(cssClass: String) {
        successors.removeCssClass(cssClass)
    }
}
