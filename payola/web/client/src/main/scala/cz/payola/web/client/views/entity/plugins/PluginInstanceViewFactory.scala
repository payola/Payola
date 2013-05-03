package cz.payola.web.client.views.entity.plugins

import cz.payola.common.entities.plugins.PluginInstance
import collection.Seq
import cz.payola.common.entities.Analysis
import s2js.compiler.javascript

class PluginInstanceViewFactory
{

    @javascript(""" var defined = eval("typeof(cz.payola.web.client.views.entity.plugins.custom."+name+"EditablePluginInstanceView) !== 'undefined'"); console.log(name, defined); return defined; """)
    private def hasEditableOverride(name: String) : Boolean = true

    @javascript(""" return eval("new cz.payola.web.client.views.entity.plugins.custom."+name+"EditablePluginInstanceView(analysis, pluginInst, predecessors)"); """)
    private def createEditableOverride(name: String, analysis: Analysis, pluginInst: PluginInstance, predecessors: Seq[PluginInstanceView] = List()): EditablePluginInstanceView = null

    def createEditable(name: String, analysis: Analysis, pluginInst: PluginInstance, predecessors: Seq[PluginInstanceView] = List()): EditablePluginInstanceView = {
        if (hasEditableOverride(name)) {
            createEditableOverride(name, analysis, pluginInst, predecessors)
        } else {
            new EditablePluginInstanceView(pluginInst, predecessors)
        }
    }
}
