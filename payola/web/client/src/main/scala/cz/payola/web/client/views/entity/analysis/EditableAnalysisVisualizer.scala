package cz.payola.web.client.views.entity.analysis

import cz.payola.common.entities.Analysis
import cz.payola.common.entities
import cz.payola.web.client.events.SimpleUnitEvent
import cz.payola.web.client.presenters.models.ParameterValue
import cz.payola.common.entities.plugins.PluginInstance
import cz.payola.web.client.views.entity.plugins._
import custom.DataCubeEditablePluginInstanceView
import cz.payola.web.client.models.PrefixApplier

/**
 *
 * @param analysis
 * @param prefixApplier
 * @author Jiri Helmich
 */
// Updated by Jiri Helmich to enable dynamic loading of PluginInstanceView
class EditableAnalysisVisualizer(analysis: Analysis, prefixApplier: PrefixApplier)
    extends AnalysisVisualizer(analysis)
{
    val parameterValueChanged = new SimpleUnitEvent[ParameterValue]

    val connectButtonClicked = new SimpleUnitEvent[EditablePluginInstanceView]

    val deleteButtonClicked = new SimpleUnitEvent[EditablePluginInstanceView]

    val instanceFactory = new PluginInstanceViewFactory(prefixApplier)

    def createPluginInstanceView(instance: PluginInstance): PluginInstanceView = {
        val view = instanceFactory.createEditable(analysis, instance, List())

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
