package cz.payola.web.client.views.entity.plugins

import cz.payola.web.client.views.elements._
import cz.payola.web.client.View
import cz.payola.common.entities.plugins._
import cz.payola.web.client.views.elements.lists._

class ReadOnlyPluginInstanceView(pluginInst: PluginInstance, predecessors: Seq[PluginInstanceView] = List())
    extends PluginInstanceView(pluginInst, predecessors)
{
    def getAdditionalControlsViews: Seq[View] = List()

    def getFooterViews: Seq[View] = {
        if (pluginInstance.plugin.name == "SPARQL Endpoint") {
            val graphUris = pluginInstance.getParameter("Graph URIs").get.toString
            val endpointURL = pluginInstance.getParameter("Endpoint URL").get.toString
            List(new Anchor(List(new Span(List(new Text("LODVis")), "label label-inverse")),
                "http://lodvisualization.appspot.com/?graphUri=" + graphUris + "&endpointUri=" + endpointURL))
        } else {
            List()
        }
    }

    def getParameterViews: Seq[View] = {
        val listItems = filterParams(getPlugin.parameters).flatMap {
            param =>
                pluginInstance.getParameter(param.name).map {
                    v =>
                        val strong = new Strong(List(new Text(parameterName(param))))
                        strong.addCssClass("param-clicked")
                        strong.mouseClicked += {
                            e =>
                                parameterNameClicked.triggerDirectly(param)
                                false
                        }

                        val item = new ListItem(List(strong, new Text(": " + v.toString)))
                        item.setAttribute("title", v.toString)
                        item
                }
        }

        List(new UnorderedList(listItems))
    }
}
