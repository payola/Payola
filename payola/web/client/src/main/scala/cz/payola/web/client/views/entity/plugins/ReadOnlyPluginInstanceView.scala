package cz.payola.web.client.views.entity.plugins

import cz.payola.web.client.views.elements._
import cz.payola.web.client.View
import scala.collection._
import cz.payola.common.entities.plugins._
import cz.payola.web.client.views.elements.lists._

class ReadOnlyPluginInstanceView(pluginInst: PluginInstance, predecessors: Seq[PluginInstanceView] = List())
    extends PluginInstanceView(pluginInst, predecessors)
{
    def getAdditionalControlsViews: Seq[View] = List()

    def getParameterViews: Seq[View] = {
        val listItems = getPlugin.parameters.flatMap {param =>
            pluginInstance.getParameter(param.name).map {v =>
                new ListItem(List(new Strong(List(new Text(param.name))), new Text(": " + v.toString)))
            }
        }

        List(new UnorderedList(listItems))
    }
}
