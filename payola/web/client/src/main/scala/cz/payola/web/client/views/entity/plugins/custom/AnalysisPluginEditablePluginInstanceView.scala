package cz.payola.web.client.views.entity.plugins.custom

import cz.payola.web.client.presenters.models.ParameterValue
import cz.payola.web.client.events._
import cz.payola.web.client.views.elements._
import cz.payola.web.client.View
import cz.payola.common.entities.plugins._
import cz.payola.web.client.views.elements.form.fields._
import cz.payola.web.client.views.bootstrap._
import cz.payola.web.shared.AnalysisRunner
import cz.payola.common.entities.Analysis
import cz.payola.common._
import cz.payola.web.client.views.graph.SimpleGraphView
import parameters._
import scala._
import scala.collection.mutable.ArrayBuffer
import scala.collection.Seq
import cz.payola.web.client.views.entity.plugins._
import cz.payola.common.EvaluationInProgress
import cz.payola.common.EvaluationError
import cz.payola.common.EvaluationSuccess
import s2js.adapters.browser._
import cz.payola.web.client.models.PrefixApplier

/**
 * Analysis Editable plugin instance visualization
 * @param pluginInst plugin instance to visualize
 * @param predecessors
 * @author Jiri Helmich
 */
class AnalysisPluginEditablePluginInstanceView(analysis: Analysis, pluginInst: PluginInstance,
    predecessors: Seq[PluginInstanceView] = List())
    extends EditablePluginInstanceView(pluginInst, predecessors, new PrefixApplier())
{
    private def name = pluginInstance.plugin.name.split("_").apply(0)

    override def getHeading: Seq[View] = List(new Heading(List(new Text("Analysis: "+name)), 3))

    override def parameterName(param: Parameter[_]): String = {
        param.name.split("""$""").apply(0)
    }

    override def filterParams(parameters: Seq[Parameter[_]]): Seq[Parameter[_]] = parameters.filterNot(_.name == "Analysis ID")
}