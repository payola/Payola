package cz.payola.web.client.views.todo

import scala.collection._
import s2js.adapters.js.html
import s2js.compiler.javascript
import cz.payola.common.entities.Plugin
import cz.payola.web.client.views.elements._
import cz.payola.web.client.View
import cz.payola.web.client.views.elements.Div

abstract class PluginInstanceView(
    val id: String,
    val plugin: Plugin,
    var predecessors: Seq[PluginInstanceView] = Nil,
    defaultValues: Map[String, String] = immutable.HashMap.empty[String, String])
    extends View
{
    private val heading = new Heading(List(new Text(plugin.name)), 3)

    private val paramsDiv = new Div(getParameterViews, "parameters")

    private val controlViews = getAdditionalControlsViews

    private val additionalControls = new Div(controlViews, "controls")

    private val alertDiv = new Div(List(heading, paramsDiv, additionalControls), "alert alert-info instance")

    private val clearSpan = new Span(List(), "clear")

    private val successors = new Div(List(clearSpan, alertDiv), "successors")

    def getParameterViews: Seq[View]

    def getAdditionalControlsViews: Seq[View]

    def getPlugin: Plugin = plugin

    def getId: String = id

    private var parentElement: Option[html.Element] = None

    def render(parent: html.Element) {
        this.parentElement = Some(parent)
        successors.render(parent)

        var i = 0
        while (i < plugin.inputCount)
        {
            val conn = new Div(Nil,"connector connector-"+i.toString)
            conn.setAttribute("style","left:"+(310*i+148)+"px")
            conn.render(successors.htmlElement)
            i += 1
        }

        var j = 0
        while (j < plugin.inputCount)
        {
            val plumb = new Div(Nil,"plumb plumb-"+i.toString)
            plumb.setAttribute("style","left:"+(310*j+145)+"px")
            plumb.render(alertDiv.htmlElement)
            j += 1
        }

        if (predecessors.nonEmpty) {
            parent.insertBefore(successors.htmlElement, predecessors(0).domElement)
        }

        predecessors.foreach {
            p =>
                successors.htmlElement.insertBefore(p.domElement, clearSpan.htmlElement)
        }
    }

    override def destroy() {
        if (parentElement.isDefined) {
            predecessors.map {
                p =>
                    parentElement.get.insertBefore(p.domElement, domElement)
            }
            parentElement.get.removeChild(domElement)
        }
    }

    def domElement: html.Element = {
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

    def setRunning(){
        clearStyle()
        alertDiv.addCssClass("alert-warning")
    }

    def setEvaluated(){
        clearStyle()
        alertDiv.addCssClass("alert-success")
    }

    var hasError = false

    def setError(message: String){
        if (!hasError){
            clearStyle()
            alertDiv.addCssClass("alert-danger")
            hasError = true
            alertDiv.setAttribute("rel","popover")
            alertDiv.setAttribute("data-content",message)
            alertDiv.setAttribute("data-original-title","Error details")
            activatePopover(alertDiv.htmlElement)
        }
    }

    @javascript("""jQuery(e).popover()""")
    def activatePopover(e: html.Element){}

    def clearStyle(){
        alertDiv.removeCssClass("alert-warning")
        alertDiv.removeCssClass("alert-success")
        alertDiv.removeCssClass("alert-info")
        alertDiv.removeCssClass("alert-danger")
    }

    def blockHtmlElement: html.Element = successors.htmlElement
}
