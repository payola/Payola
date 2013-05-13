package cz.payola.web.client.views.entity.analysis

import cz.payola.web.client.views.elements._
import cz.payola.common.entities.Analysis
import cz.payola.web.client.View
import s2js.adapters.html
import scala.collection.mutable.ArrayBuffer
import cz.payola.common.entities
import scala.collection.mutable.HashMap
import cz.payola.web.client.events.SimpleUnitEvent
import entities.plugins._
import cz.payola.web.client.views.ComposedView
import cz.payola.web.client.views.entity.plugins.PluginInstanceView

abstract class AnalysisVisualizer(analysis: Analysis) extends ComposedView
{
    val pluginInstanceRendered = new SimpleUnitEvent[PluginInstanceView]
    val paramNameClicked = new SimpleUnitEvent[ParameterValue[_]]

    private val pluginCanvas = new Div(Nil, "plugin-canvas")

    protected val instancesMap: HashMap[String, PluginInstanceView] = new HashMap[String, PluginInstanceView]

    def createSubViews: Seq[View] = {
        List(pluginCanvas)
    }

    override def render(parent: html.Element) {
        super.render(parent)
        renderAnalysis()
    }

    private def renderAnalysis() {
        val sources = new ArrayBuffer[PluginInstanceView]
        val renderBuffer = new ArrayBuffer[PluginInstanceView]

        fillRenderBuffers(analysis, sources, renderBuffer)
        setPredecessorsFromBindings(analysis)
        renderSources(sources, renderBuffer)
        renderBufferTopologically(renderBuffer)
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
        sources.map { s =>
            renderPluginInstanceView(s)
            renderBuffer -= s
        }
    }

    private def setPredecessorsFromBindings(analysis: entities.Analysis) {
        analysis.pluginInstanceBindings.sortWith((a,b) => (a.targetInputIndex < b.targetInputIndex)).map { b =>
            val buff = new ArrayBuffer[PluginInstanceView]()
            instancesMap(b.targetPluginInstance.id).predecessors.map(buff.append(_))
            buff.append(instancesMap(b.sourcePluginInstance.id))
            instancesMap(b.targetPluginInstance.id).predecessors = buff
        }
    }

    protected def fillRenderBuffers(analysis: entities.Analysis, sources: ArrayBuffer[PluginInstanceView],
        renderBuffer: ArrayBuffer[PluginInstanceView]) {
        analysis.pluginInstances.map { instance =>
            val clientInstance = createPluginInstanceView(instance)
            instancesMap.put(instance.id, clientInstance)

            if (isSource(instance, analysis)) {
                sources += clientInstance
            }

            renderBuffer += clientInstance
        }
    }

    def createPluginInstanceView(instance: PluginInstance): PluginInstanceView

    private def isSource(instance: entities.plugins.PluginInstance, analysis: Analysis): Boolean = {
        !analysis.pluginInstanceBindings.find(_.targetPluginInstance == instance).isDefined
    }

    def renderPluginInstanceView(v: PluginInstanceView) {
        v.render(pluginCanvas.htmlElement)
        v.parameterNameClicked += { e => paramNameClicked.triggerDirectly(e.target) }
        pluginInstanceRendered.triggerDirectly(v)
    }
}
