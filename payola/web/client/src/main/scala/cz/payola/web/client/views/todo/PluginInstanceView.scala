package cz.payola.web.client.views.todo

import s2js.adapters.js.dom
import cz.payola.common.entities.Plugin
import s2js.compiler.javascript
import scala.collection._
import cz.payola.web.client.presenters.models.ParameterValue
import cz.payola.web.client.views.elements._
import cz.payola.web.client.events._
import cz.payola.web.client.View
import cz.payola.web.client.views.bootstrap._
import cz.payola.web.client.views.elements.Div
import scala.Seq
import scala.collection.immutable.HashMap
import scala.Some
import s2js.adapters.js.dom.Element
import cz.payola.web.client.views.ComposedView

abstract class PluginInstanceView(
    val id: String,
    val plugin: Plugin,
    var predecessors: Seq[PluginInstanceView] = Nil,
    defaultValues: Map[String, String] = HashMap.empty[String, String])
    extends View
{
    private val heading = new Heading(List(new Text(plugin.name)), 3)

    private val paramsDiv = new Div(getParameterViews)
    private val controlViews = getAdditionalControlsViews
    private val additionalControls = new Div(controlViews, "controls")

    private val alertDiv = new Div(List(heading, paramsDiv, additionalControls), "alert alert-info instance")
    private val clearSpan = new Span(List(), "clear")
    private val successors = new Div(List(clearSpan, alertDiv), "successors")

    def getParameterViews : Seq[View]
    def getAdditionalControlsViews : Seq[View]

    def getPlugin: Plugin = plugin
    def getId: String = id

    private var parentElement: Option[dom.Element] = None

    def render(parent: dom.Element) {
        this.parentElement = Some(parent)
        successors.render(parent)

        if (predecessors.nonEmpty) {
            parent.insertBefore(successors.domElement, predecessors(0).domElement)
        }

        predecessors.foreach{ p =>
            successors.domElement.insertBefore(p.domElement, clearSpan.domElement)
        }
    }

    override def destroy() {
        if (parentElement.isDefined) {
            unbindJsPlumb(getPluginElement)
            predecessors.map{ p =>
                parentElement.get.insertBefore(p.domElement, domElement)
            }
            parentElement.get.removeChild(domElement)
        }
    }

    @javascript( """
                   var connections = jsPlumb.getConnections({target: element.getAttribute("id")});
                   for (var k in connections){ jsPlumb.detach(connections[k]); }
                 """)
    def unbindJsPlumb(element: dom.Element) {}

    def domElement: dom.Element = {
        successors.domElement
    }

    def getPluginElement: dom.Element = {
        alertDiv.domElement
    }

    def showControls() {
        additionalControls.show()
    }

    def hideControls() {
        additionalControls.hide()
    }

    def blockDomElement: Element = successors.domElement
}
