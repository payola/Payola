package cz.payola.web.client.views.entity.analysis

import cz.payola.common.entities.Analysis
import cz.payola.web.client.views.todo._
import cz.payola.common.entities.plugins.PluginInstance

class ReadOnlyAnalysisVisualizer(analysis: Analysis) extends AnalysisVisualizer(analysis)
{
    def createPluginInstanceView(instance: PluginInstance): PluginInstanceView = {
        val defaultValues = getDefaultValues(instance)
        val result = new ReadOnlyPluginInstanceView(instance.id, instance.plugin, List(), defaultValues)
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
}
