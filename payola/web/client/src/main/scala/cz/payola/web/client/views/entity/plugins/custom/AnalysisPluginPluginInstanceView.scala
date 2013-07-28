package cz.payola.web.client.views.entity.plugins.custom

import cz.payola.web.client.views.elements._
import cz.payola.web.client.View
import cz.payola.common.entities.plugins._
import scala._
import scala.collection.Seq
import cz.payola.web.client.views.entity.plugins._
import cz.payola.web.client.models.PrefixApplier

/**
 * Analysis plugin instance visualization
 * @param pluginInst plugin instance to visualize
 * @param predecessors
 * @author Jiri Helmich
 */
class AnalysisPluginPluginInstanceView(pluginInst: PluginInstance,
    predecessors: Seq[PluginInstanceView] = List())
    extends ReadOnlyPluginInstanceView(pluginInst, predecessors, new PrefixApplier())
{
    private def name = pluginInstance.plugin.name.split("_").apply(0)

    override def getHeading: Seq[View] = List(new Heading(List(new Text("Analysis: "+name)), 3))

    override def parameterName(param: Parameter[_]): String = {
        param.name.split("""$""").apply(0)
    }

    override def filterParams(parameters: Seq[Parameter[_]]): Seq[Parameter[_]] = parameters.filterNot(_.name == "Analysis ID")
}