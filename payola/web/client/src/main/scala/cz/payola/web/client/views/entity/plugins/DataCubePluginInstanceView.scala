package cz.payola.web.client.views.entity.plugins

import cz.payola.web.client.views.elements._
import cz.payola.web.client.View
import cz.payola.common.entities.plugins._
import cz.payola.common.entities.Analysis
import scala._
import scala.collection.Seq
import cz.payola.web.client.views.elements.lists._

class DataCubePluginInstanceView(analysis: Analysis, pluginInst: PluginInstance,
    predecessors: Seq[PluginInstanceView] = List())
    extends ReadOnlyPluginInstanceView(pluginInst, predecessors)
{
    private def name = {
        val nameParts = pluginInstance.plugin.name.split("#")
        if (nameParts.length > 1) nameParts(1) else pluginInstance.plugin.name
    }

    override def getHeading: Seq[View] = List(new Heading(List(new Text("DataCube Vocabulary")), 3),
        new Paragraph(List(new Text(name))))

    override def getParameterViews = {

        val list = pluginInstance.plugin.parameters.map{ p =>
            new ListItem(List(new Text(p.name)))
        }

        val paramVals = pluginInstance.parameterValues.map { v =>
            new Text(v.value.toString)
        }

        List(new UnorderedList(list), new Paragraph(paramVals))
    }
}
