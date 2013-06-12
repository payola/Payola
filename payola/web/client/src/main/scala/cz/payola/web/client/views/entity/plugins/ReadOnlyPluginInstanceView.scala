package cz.payola.web.client.views.entity.plugins

import cz.payola.web.client.views.elements._
import cz.payola.web.client.View
import cz.payola.common.entities.plugins._
import cz.payola.web.client.views.elements.lists._
import cz.payola.web.client.models.PrefixApplier

class ReadOnlyPluginInstanceView(pluginInst: PluginInstance, predecessors: Seq[PluginInstanceView] = List(),
    prefixApplier: PrefixApplier) extends PluginInstanceView(pluginInst, predecessors, prefixApplier)
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
                        strong.mouseClicked += {
                            e =>
                                strong.addCssClass("param-clicked")
                                pluginInstance.getParameterValue(param.name).foreach(parameterNameClicked.triggerDirectly(_))
                                false
                        }

                        val item = new ListItem(List(strong, new Text(": " + prefixApplier.applyPrefix(v.toString))))
                        item.setAttribute("title", v.toString)
                        item
                }
        }

        List(new UnorderedList(listItems))
    }
}
