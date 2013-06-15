package cz.payola.web.client.views.entity.plugins

import custom._
import cz.payola.common.entities.plugins.PluginInstance
import collection.Seq
import cz.payola.common.entities.Analysis
import s2js.compiler.javascript
import cz.payola.web.client.models.PrefixApplier

class PluginInstanceViewFactory(prefixApplier: PrefixApplier)
{
    @javascript(
        """ var defined = eval("typeof(cz.payola.web.client.views.entity.plugins.custom."+name+"EditablePluginInstanceView) !== 'undefined'"); return defined; """)
    private def hasEditableOverride(name: String): Boolean = true

    @javascript(
        """ return eval("new cz.payola.web.client.views.entity.plugins.custom."+name+"EditablePluginInstanceView(analysis, pluginInst, predecessors)"); """)
    private def createEditableOverride(name: String, analysis: Analysis, pluginInst: PluginInstance,
        predecessors: Seq[PluginInstanceView] = List()): EditablePluginInstanceView = null

    def createEditable(analysis: Analysis, pluginInstance: PluginInstance,
        predecessors: Seq[PluginInstanceView] = List()): EditablePluginInstanceView = {
        /*if (hasEditableOverride(name)) {
            createEditableOverride(name, analysis, pluginInst, predecessors)
        } else {
            new EditablePluginInstanceView(pluginInst, predecessors)
        }*/
        pluginInstance.plugin.originalClassName match {
            case "DataCube" => new DataCubeEditablePluginInstanceView(analysis, pluginInstance, predecessors)
            case "AnalysisPlugin" => new AnalysisPluginEditablePluginInstanceView(pluginInstance, predecessors)
            case _ => new EditablePluginInstanceView(pluginInstance, predecessors, prefixApplier)
        }
    }

    def create(pluginInstance: PluginInstance, predecessors: Seq[PluginInstanceView] = List()): ReadOnlyPluginInstanceView = {
        pluginInstance.plugin.originalClassName match {
            case "DataCube" => new DataCubePluginInstanceView(pluginInstance, predecessors)
            case "AnalysisPlugin" => new AnalysisPluginPluginInstanceView(pluginInstance, predecessors)
            case _ => new ReadOnlyPluginInstanceView(pluginInstance, predecessors, prefixApplier)
        }
    }
}
