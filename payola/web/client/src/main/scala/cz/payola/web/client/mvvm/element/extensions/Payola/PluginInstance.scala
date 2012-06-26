package cz.payola.web.client.mvvm.element.extensions.Payola

import cz.payola.web.client.mvvm.element._
import cz.payola.common.entities.analyses.Parameter
import cz.payola.web.client.mvvm.Component
import s2js.adapters.js.browser.document
import s2js.adapters.js.dom.Element
import cz.payola.web.client.mvvm.element.extensions.Bootstrap.Button
import cz.payola.web.client.events._

class PluginInstance(pluginType: String, predecessors: Option[PluginInstance]) extends Component
{
    val connectButtonClicked = new ClickedEvent[PluginInstance]

    val heading = new Heading3(List(new Text(pluginType)))

    /*val list = parameters.keys.map{key =>
        val strong = new Strong(List(new Text(key)))

        val parameter = parameters.get(key).get
        val field = new Input(parameter.name,"", None)

        new Paragraph(List(strong, new Text(":"), field))
    }

    val paramsDiv = new Div(list.toList)*/
    val connect = new Button("Add connection")
    val delete = new Button("Delete","btn-danger")

    val alertDiv = new Div(List(heading,connect, delete),"alert alert-info instance")
    val clearSpan = new Span(List(), "clear")

    val successors = new Div(List(clearSpan,alertDiv),"successors")

    def render(parent: Element = document.body) = {
        successors.render(parent)
        if (predecessors.isDefined)
        {
            successors.div.insertBefore(predecessors.get.successors.div, clearSpan.span)
        }
    }

    override def destroy() = {

    }

    connect.clicked += { evt =>
        connectButtonClicked.trigger(new ClickedEventArgs[PluginInstance](this))
        false
    }
}
