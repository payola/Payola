package cz.payola.web.client.views.todo

import cz.payola.common.entities.Plugin
import cz.payola.web.client.views.elements._
import cz.payola.web.client.View
import scala.collection.immutable.HashMap
import scala.collection._
import cz.payola.web.client.views.elements.lists._

class ReadOnlyPluginInstanceView(id: String, plugin: Plugin, predecessors: Seq[PluginInstanceView] = List(),
    defaultValues: Map[String, String] = new HashMap[String, String]())
    extends PluginInstanceView(id, plugin, predecessors, defaultValues)
{
    def getAdditionalControlsViews: Seq[View] = List()

    def getParameterViews: Seq[View] = {
        val listItems = getPlugin.parameters.map { param =>

            val defaultVal = if (defaultValues.isDefinedAt(param.name)) {
                defaultValues(param.name)
            }
            else {
                param.defaultValue.toString
            }
            new ListItem(List(new Strong(List(new Text(param.name))), new Text(": " + defaultVal)))
        }

        List(new UnorderedList(listItems))
    }
}
