package cz.payola.web.client.views.todo

import s2js.adapters.js.browser.document
import s2js.adapters.js.dom._
import cz.payola.common.entities.Plugin
import s2js.compiler.javascript
import scala.collection.mutable
import cz.payola.web.client.presenters.models.ParameterValue
import cz.payola.web.client.views.events._
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.extensions.bootstrap._
import cz.payola.web.client.events._
import scala.Some
import cz.payola.web.client.views.Component
import cz.payola.web.client.views.extensions.bootstrap.Button
import cz.payola.web.client.views.elements.Div
import scala.Some

object PluginInstance
{
    var counter = 0

    def getCounter() = {
        counter += 1
        counter
    }
}

class PluginInstance(val id: String, val plugin: Plugin, var predecessors: Seq[PluginInstance] = List())
    extends Component
{
    val connectButtonClicked = new SimpleEvent[PluginInstance]

    val deleteButtonClicked = new SimpleEvent[PluginInstance]

    val parameterValueChanged = new SimpleEvent[ParameterValue]

    private val heading = new Heading(List(new Text(plugin.name)), 3)

    private val params = new mutable.HashMap[Int, InputControl]

    var paramIdx = 0

    private val list = plugin.parameters.map { param =>

        val field = new InputControl(param.name, param.id, "", "Enter parameter value")

        field.changed += { args =>
            parameterValueChanged.triggerDirectly(new ParameterValue(id, param.id, param.name, field.getValue(), field))
        }

        params.put(paramIdx, field)
        paramIdx += 1

        field
    }

    private val paramsDiv = new Div(list)

    private val connect = new Button("Add connection")

    private val delete = new Button("Delete", "btn-danger")

    private val alertDiv = new Div(List(heading, paramsDiv, connect, delete), "alert alert-info instance")

    private val clearSpan = new Span(List(), "clear")

    private val successors = new Div(List(clearSpan, alertDiv), "successors")

    connect.mouseClicked += { e =>
        connectButtonClicked.triggerDirectly(this)
        false
    }
    delete.mouseClicked += { e =>
        deleteButtonClicked.triggerDirectly(this)
        false
    }

    private var parentNode: Option[Node] = None

    def render(parent: Node) = {
        this.parentNode = Some(parent)
        alertDiv.id = (plugin.id + "_" + PluginInstance.getCounter())
        successors.render(parent)

        if (predecessors.size > 0) {
            parent.insertBefore(successors.domElement, predecessors(0).domElement)
        }

        var i = 0
        while (i < predecessors.size) {
            successors.domElement.insertBefore(predecessors(i).domElement, clearSpan.domElement)
            i += 1
        }
    }

    override def destroy() = {
        if (parentNode.isDefined) {
            unbindJsPlumb(getPluginElement)
            var i = 0
            while (i < predecessors.size) {
                parentNode.get.insertBefore(predecessors(i).domElement, domElement)
                i += 1
            }
            parentNode.get.removeChild(domElement)
        }
    }

    @javascript( """
                   var connections = jsPlumb.getConnections({target: element.getAttribute("id")});
                   for (var k in connections){ jsPlumb.detach(connections[k]); }
                 """)
    def unbindJsPlumb(element: Element) = {}

    def domElement: Element = {
        successors.domElement
    }

    def getPluginElement: Element = {
        alertDiv.domElement
    }

    def showDeleteButton() = {
        delete.domElement.setAttribute("style", "display: inline-block;")
    }

    def hideDeleteButton() = {
        delete.domElement.setAttribute("style", "display: none;")
    }

    def getParamValue(index: Int) = {
        params(index).getValue()
    }
}
