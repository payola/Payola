package cz.payola.web.client.views.entity.analysis

import cz.payola.common.entities.Analysis
import cz.payola.common.entities
import cz.payola.web.client.events.SimpleUnitEvent
import cz.payola.web.client.presenters.models.ParameterValue
import cz.payola.common.entities.plugins.PluginInstance
import cz.payola.web.client.views.entity.plugins._
import cz.payola.common.entities.plugins.parameters.StringParameter

class EditableAnalysisVisualizer(analysis: Analysis) extends AnalysisVisualizer(analysis)
{
    val parameterValueChanged = new SimpleUnitEvent[ParameterValue]

    val connectButtonClicked = new SimpleUnitEvent[EditablePluginInstanceView]

    val deleteButtonClicked = new SimpleUnitEvent[EditablePluginInstanceView]

    def createPluginInstanceView(instance: PluginInstance): PluginInstanceView = {
        val patterns = instance.plugin.parameters.forall {
            x =>
                x match {
                    case p: StringParameter => p.isPattern
                    case _ => false
                }
        }

        val view = if (patterns) {
            new DataCubeEditablePluginInstanceView(analysis, instance, List())
        } else {
            new EditablePluginInstanceView(instance, List())
        }

        initializeEditableInstance(view, instance, analysis)
        view
    }

    private def initializeEditableInstance(instanceView: EditablePluginInstanceView,
        instance: entities.plugins.PluginInstance,
        analysis: Analysis) {
        instanceView.hideControls()

        if (instanceHasNoFollowers(analysis, instance)) {
            instanceView.showControls()
        }

        instanceView.parameterValueChanged += {
            e => parameterValueChanged.triggerDirectly(e.target)
        }
        instanceView.connectButtonClicked += {
            e => connectButtonClicked.triggerDirectly(e.target)
        }
        instanceView.deleteButtonClicked += {
            e => deleteButtonClicked.triggerDirectly(e.target)
        }
    }

    private def instanceHasNoFollowers(analysis: Analysis, instance: entities.plugins.PluginInstance): Boolean = {
        !analysis.pluginInstanceBindings.find(_.sourcePluginInstance == instance).isDefined
    }
}
