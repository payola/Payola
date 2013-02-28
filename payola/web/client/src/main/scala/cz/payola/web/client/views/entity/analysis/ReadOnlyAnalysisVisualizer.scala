package cz.payola.web.client.views.entity.analysis

import cz.payola.common.entities.Analysis
import cz.payola.common.entities.plugins.PluginInstance
import cz.payola.web.client.views.entity.plugins._
import cz.payola.common.entities.plugins.parameters.StringParameter

class ReadOnlyAnalysisVisualizer(analysis: Analysis) extends AnalysisVisualizer(analysis)
{
    def createPluginInstanceView(instance: PluginInstance): PluginInstanceView = {

        val patterns = instance.plugin.parameters.forall {
            x =>
                x match {
                    case p: StringParameter => p.isPattern
                    case _ => false
                }
        }

        val result = if (patterns) {
            new DataCubePluginInstanceView(analysis, instance, List())
        } else {
            new ReadOnlyPluginInstanceView(instance, List())
        }

        instancesMap.put(instance.id, result)
        result
    }

    def setInstanceError(instanceId: String, message: String) {
        instancesMap.get(instanceId).map(_.setError(message))
    }

    def setInstanceEvaluated(instanceId: String) {
        instancesMap.get(instanceId).map(_.setEvaluated())
    }

    def setInstanceRunning(instanceId: String) {
        instancesMap.get(instanceId).map(_.setRunning())
    }

    def setAllDone() {
        instancesMap.foreach(_._2.setEvaluated())
    }

    def clearAllAttributes() {
        instancesMap foreach { case (key, view) =>
            view.clearStyle()
        }
    }
}
