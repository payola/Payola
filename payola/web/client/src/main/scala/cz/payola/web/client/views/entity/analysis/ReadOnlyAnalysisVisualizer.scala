package cz.payola.web.client.views.entity.analysis

import cz.payola.common.entities.Analysis
import cz.payola.web.client.views.todo._
import cz.payola.common.entities
import cz.payola.web.client.events.SimpleUnitEvent
import cz.payola.web.client.presenters.models.ParameterValue
import cz.payola.common.entities.plugins.PluginInstance

class ReadOnlyAnalysisVisualizer(analysis: Analysis) extends AnalysisVisualizer(analysis)
{
    def createPluginInstanceView(instance: PluginInstance): PluginInstanceView = {
        val defaultValues = getDefaultValues(instance)
        new ReadOnlyPluginInstanceView(instance.id, instance.plugin, List(), defaultValues)
    }
}
