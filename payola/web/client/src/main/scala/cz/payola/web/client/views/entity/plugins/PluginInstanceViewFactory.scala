package cz.payola.web.client.views.entity.plugins

import cz.payola.common.entities.plugins.PluginInstance
import collection.Seq
import cz.payola.common.entities.Analysis
import s2js.compiler.javascript
import s2js.runtime.shared.DependencyProvider
import s2js.runtime.client.scala.collection.mutable.HashMap
import cz.payola.web.client.models.PrefixApplier

class PluginInstanceViewFactory(prefixApplier: PrefixApplier)
{
    private val registry = new HashMap[String, Boolean]

    @javascript(
        """ var defined = eval("(typeof(cz.payola.web.client.views.entity.plugins.custom) !== 'undefined') && (typeof(cz.payola.web.client.views.entity.plugins.custom."+name+"EditablePluginInstanceView) !== 'undefined')"); return defined; """)
    private def hasEditableOverride(name: String): Boolean = true

    @javascript(
        """ var defined = eval("(typeof(cz.payola.web.client.views.entity.plugins.custom) !== 'undefined') && (typeof(cz.payola.web.client.views.entity.plugins.custom."+name+"PluginInstanceView) !== 'undefined')"); return defined; """)
    private def hasOverride(name: String): Boolean = true

    @javascript(" return eval(x) ")
    private def eval(x: String){}

    @javascript(
        """ return eval("new cz.payola.web.client.views.entity.plugins.custom."+name+"EditablePluginInstanceView(analysis, pluginInst, predecessors)"); """)
    private def createEditableOverride(name: String, analysis: Analysis, pluginInst: PluginInstance,
        predecessors: Seq[PluginInstanceView] = List()): EditablePluginInstanceView = null

    @javascript(
        """ return eval("new cz.payola.web.client.views.entity.plugins.custom."+name+"PluginInstanceView(pluginInst, predecessors)"); """)
    private def createOverride(name: String, pluginInst: PluginInstance,
        predecessors: Seq[PluginInstanceView] = List()): ReadOnlyPluginInstanceView = null

    def createEditable(analysis: Analysis, pluginInstance: PluginInstance,
        predecessors: Seq[PluginInstanceView] = List()): EditablePluginInstanceView = {

        load("cz.payola.web.client.views.entity.plugins.custom."+pluginInstance.plugin.originalClassName+"EditablePluginInstanceView")

        if (hasEditableOverride(pluginInstance.plugin.originalClassName)) {
            createEditableOverride(pluginInstance.plugin.originalClassName, analysis, pluginInstance, predecessors)
        } else {
            new EditablePluginInstanceView(pluginInstance, predecessors, prefixApplier)
        }
    }

    def create(pluginInstance: PluginInstance, predecessors: Seq[PluginInstanceView] = List()): ReadOnlyPluginInstanceView = {

        load("cz.payola.web.client.views.entity.plugins.custom."+pluginInstance.plugin.originalClassName+"PluginInstanceView")

        if (hasOverride(pluginInstance.plugin.originalClassName)){
            createOverride(pluginInstance.plugin.originalClassName, pluginInstance, predecessors)
        } else {
            new ReadOnlyPluginInstanceView(pluginInstance, predecessors, prefixApplier)
        }
    }

    private def load(className: String){
        if (!registry.isDefinedAt(className) || !registry(className)){
            eval(DependencyProvider.get(List(className),List()).javaScript)
            registry.put(className, true)
        }
    }
}
