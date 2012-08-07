package cz.payola.web.client.views.todo

import cz.payola.web.client.views.elements._
import cz.payola.web.client.View
import scala.collection._
import cz.payola.common.entities.plugins._

class ReadOnlyPluginInstanceView(pluginInst: PluginInstance, predecessors: Seq[PluginInstanceView] = List())
    extends PluginInstanceView(pluginInst, predecessors)
{
    def getAdditionalControlsViews : Seq[View] = List()

    def getParameterViews : Seq[View] = {
        val listItems = getPlugin.parameters.map { param =>
            val instanceVal = pluginInstance.getParameter(param.name)

            val defaultVal = instanceVal.getOrElse("").toString
            new ListItem(List(new Strong(List(new Text(param.name))),new Text(": "+defaultVal)))
        }

        List(new UnorderedList(listItems))
    }
}
