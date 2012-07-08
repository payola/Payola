package cz.payola.web.client.mvvm.element.extensions.Payola

import cz.payola.web.client.mvvm.element._
import cz.payola.web.client.mvvm.Component
import s2js.adapters.js.browser.document
import s2js.adapters.js.dom.Element
import cz.payola.web.client.mvvm.element.extensions.Bootstrap.Button
import cz.payola.web.client.events._
import cz.payola.common.entities.Plugin
import s2js.compiler.javascript
import scala.collection.mutable

object PluginInstance
{
    var counter = 0
    def getCounter() = {
        counter += 1
        counter
    }
}

class PluginInstance(val plugin: Plugin, val predecessors: Seq[PluginInstance] = List()) extends Component
{
    val connectButtonClicked = new ClickedEvent[PluginInstance]
    val deleteButtonClicked = new ClickedEvent[PluginInstance]

    private val heading = new Heading3(List(new Text(plugin.name)))
    private val params = new mutable.HashMap[Int,Input]

    var paramIdx = 0
    private val list = plugin.parameters.map { param =>
        val strong = new Strong(List(new Text(param.name)))
        val field = new Input(param.id, "", None)
        params.put(paramIdx, field)
        paramIdx+=1

        new Paragraph(List(strong, new Text(":"), field))
    }

    private val paramsDiv = new Div(list)
    private val connect = new Button("Add connection")
    private val delete = new Button("Delete", "btn-danger")
    private val alertDiv = new Div(List(heading, paramsDiv, connect, delete), "alert alert-info instance")
    private val clearSpan = new Span(List(), "clear")
    private val successors = new Div(List(clearSpan, alertDiv), "successors")

    connect.clicked += { evt =>
        connectButtonClicked.trigger(new ClickedEventArgs[PluginInstance](this))
        false
    }
    delete.clicked += { evt =>
        deleteButtonClicked.trigger(new ClickedEventArgs[PluginInstance](this))
        false
    }

    private var parent: Option[Element] = None

    def render(parent: Element = document.body) = {
        this.parent = Some(parent)
        alertDiv.setId(plugin.id+"_"+PluginInstance.getCounter())
        successors.render(parent)

        if (predecessors.size > 0) {
            parent.insertBefore(successors.getDomElement, predecessors(0).getDomElement)
        }

        var i = 0
        while (i < predecessors.size) {
            successors.getDomElement.insertBefore(predecessors(i).getDomElement, clearSpan.getDomElement)
            i += 1
        }
    }

    override def destroy() = {
        if (parent.isDefined) {
            unbindJsPlumb(getPluginElement)
            var i = 0
            while (i < predecessors.size) {
                parent.get.insertBefore(predecessors(i).getDomElement, getDomElement)
                i += 1
            }
            parent.get.removeChild(getDomElement)
        }
    }

    @javascript(""" var connections = jsPlumb.getConnections({target: element.getAttribute("id")}); for (var k in connections){ jsPlumb.detach(connections[k]); } """)
    def unbindJsPlumb(element: Element) = {}

    def getDomElement: Element = {
        successors.getDomElement
    }

    def getPluginElement: Element = {
        alertDiv.getDomElement
    }

    def showDeleteButton() = {
        delete.getDomElement.setAttribute("style","display: inline-block;")
    }

    def hideDeleteButton() = {
        delete.getDomElement.setAttribute("style","display: none;")
    }

    def getParamValue(index: Int) = {
        params(index).getText
    }
}
