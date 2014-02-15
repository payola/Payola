package cz.payola.web.client.views.entity.plugins

import cz.payola.web.client.views.elements._
import cz.payola.web.client.View
import cz.payola.common.entities.plugins._
import cz.payola.web.client.views.elements.lists._
import cz.payola.web.client.models.PrefixApplier
import cz.payola.common.entities.plugins.parameters.StringParameter
import s2js.compiler.javascript

class ReadOnlyPluginInstanceView(pluginInst: PluginInstance, predecessors: Seq[PluginInstanceView] = List(),
    prefixApplier: PrefixApplier) extends PluginInstanceView(pluginInst, predecessors, prefixApplier)
{
    def getAdditionalControlsViews: Seq[View] = List()

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

                        // If rendering string parameter that can contain url, try to find matching prefix
                        val item = param match {
                            case p : StringParameter if p.canContainUrl => new ListItem(List(strong, new Text(": " + prefixApplier.applyPrefix(v.toString))))
                            case p : StringParameter if p.isPassword => new ListItem(List(strong, new Text(": ***")))
                            case p : StringParameter if p.isMultiline => {
                                new ListItem(List(strong, new Text(": "))++v.toString.split("\n").toList.map{ t => new Paragraph(List(new Text(t))) })
                            }
                            case _ => new ListItem(List(strong, new Text(": "+v.toString)))
                        }

                        item.setAttribute("title", v.toString)
                        item
                }
        }

        List(new UnorderedList(listItems, "list-unstyled readonly"))
    }
}
