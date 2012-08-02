package cz.payola.web.client.views.entity.analysis

import cz.payola.web.client.views.elements._
import cz.payola.common.entities.Analysis
import cz.payola.web.client.View
import s2js.adapters.js.html
import scala.collection.mutable.ArrayBuffer
import cz.payola.web.client.views.todo._
import cz.payola.common.entities
import s2js.runtime.client.scala.collection.mutable.HashMap
import scala.collection.mutable
import s2js.compiler.javascript
import cz.payola.web.client.events.SimpleUnitEvent
import cz.payola.common.entities.plugins.PluginInstance

abstract class AnalysisVisualizer(analysis: Analysis) extends View
{
    val pluginInstanceRendered = new SimpleUnitEvent[PluginInstanceView]

    private val pluginCanvas = new Div(Nil, "plugin-canvas")
    protected val instancesMap = new HashMap[String, PluginInstanceView]

    def render(parent: html.Element) {
        pluginCanvas.render(parent)
        renderAnalysis()
    }

    def destroy() {
        pluginCanvas.destroy()
    }

    def blockHtmlElement = pluginCanvas.htmlElement

    private def renderAnalysis() {
        val sources = new ArrayBuffer[PluginInstanceView]
        val renderBuffer = new ArrayBuffer[PluginInstanceView]

        fillRenderBuffers(analysis, sources, renderBuffer)
        setPredecessorsFromBindings(analysis)
        renderSources(sources, renderBuffer)
        renderBufferTopologically(renderBuffer)
        renderBindings(analysis)
    }

    private def renderBindings(analysis: entities.Analysis) {
        analysis.pluginInstanceBindings.map {
            b => renderBinding(instancesMap(b.sourcePluginInstance.id), instancesMap(b.targetPluginInstance.id))
        }
    }

    private def renderBufferTopologically(renderBuffer: ArrayBuffer[PluginInstanceView]) {
        while (!renderBuffer.isEmpty) {
            renderBuffer.map {
                s =>
                    val canRender: Boolean = predecessorsRendered(s, renderBuffer)
                    if (canRender) {
                        renderPluginInstanceView(s)
                        renderBuffer -= s
                    }
            }
        }
    }

    private def predecessorsRendered(s: PluginInstanceView, renderBuffer: ArrayBuffer[PluginInstanceView]): Boolean = {
        var canRender = true
        s.predecessors.map {
            predecessor => canRender = (canRender && !renderBuffer.contains(predecessor))
        }
        canRender
    }

    def renderSources(sources: ArrayBuffer[PluginInstanceView], renderBuffer: ArrayBuffer[PluginInstanceView]) {
        sources.map {
            s =>
                renderPluginInstanceView(s)
                renderBuffer -= s
        }
    }

    private def setPredecessorsFromBindings(analysis: entities.Analysis) {
        analysis.pluginInstanceBindings.map {
            b =>
                val buff = new ArrayBuffer[PluginInstanceView]()
                instancesMap(b.targetPluginInstance.id).predecessors.map(buff.append(_))
                buff.append(instancesMap(b.sourcePluginInstance.id))
                instancesMap(b.targetPluginInstance.id).predecessors = buff
        }
    }

    protected def fillRenderBuffers(analysis: entities.Analysis, sources: ArrayBuffer[PluginInstanceView],
        renderBuffer: ArrayBuffer[PluginInstanceView]) {
        analysis.pluginInstances.map {
            instance =>

                val clientInstance = createPluginInstanceView(instance)

                instancesMap.put(instance.id, clientInstance)

                if (isSource(instance, analysis)) {
                    sources += clientInstance
                }

                renderBuffer += clientInstance
        }
    }

    def createPluginInstanceView(instance: PluginInstance) : PluginInstanceView

    private def isSource(instance: entities.plugins.PluginInstance, analysis: Analysis): Boolean = {
        !analysis.pluginInstanceBindings.find(_.targetPluginInstance == instance).isDefined
    }

    protected def getDefaultValues(instance: entities.plugins.PluginInstance): mutable.HashMap[String, String] = {
        val map = new mutable.HashMap[String, String]
        instance.parameterValues.foreach {
            v => map.put(v.parameter.name, v.value.toString)
        }
        map
    }

    @javascript(
        """
        """)
    def renderBinding(a: PluginInstanceView, b: PluginInstanceView) {}

    def renderPluginInstanceView(v: PluginInstanceView){
        v.render(pluginCanvas.htmlElement)
        pluginInstanceRendered.triggerDirectly(v)
    }
}
