package cz.payola.web.client.views.entity.analysis

import cz.payola.common.entities.Analysis
import cz.payola.common.entities.plugins.PluginInstance
import cz.payola.web.client.views.entity.plugins._
import cz.payola.web.client.models.PrefixApplier

class ReadOnlyAnalysisVisualizer(analysis: Analysis, prefixApplier: PrefixApplier) extends AnalysisVisualizer(analysis)
{
    val instanceFactory = new PluginInstanceViewFactory(prefixApplier)

    def createPluginInstanceView(instance: PluginInstance): PluginInstanceView = {
        val result = instanceFactory.create(instance, List())

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
        instancesMap foreach {
            case (key, view) =>
                view.clearStyle()
        }
    }
}
