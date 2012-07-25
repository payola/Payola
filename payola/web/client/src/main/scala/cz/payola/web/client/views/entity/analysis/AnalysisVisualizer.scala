package cz.payola.web.client.views.entity.analysis

import cz.payola.web.client.views.elements._
import cz.payola.common.entities.Analysis
import cz.payola.web.client.View
import s2js.adapters.js.dom.Element
import scala.collection.mutable.ArrayBuffer
import cz.payola.web.client.views.todo._
import cz.payola.common.entities
import s2js.runtime.client.scala.collection.mutable.HashMap
import scala.collection.mutable
import s2js.compiler.javascript
import cz.payola.web.client.events.SimpleUnitEvent
import cz.payola.common.entities.plugins.PluginInstance
import cz.payola.web.client.presenters.models.ParameterValue

class AnalysisVisualizer(analysis: Analysis, editable: Boolean = false) extends View
{
    val parameterValueChanged = new SimpleUnitEvent[ParameterValue]

    private val pluginCanvas = new Div(Nil, "plugin-canvas")
    private val instancesMap = new HashMap[String, PluginInstanceView]

    def render(parent: Element) {
        pluginCanvas.render(parent)
        renderAnalysis()
    }

    def destroy() {
        pluginCanvas.destroy()
    }

    def blockDomElement = pluginCanvas.domElement

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
                        s.render(pluginCanvas.domElement)
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
                s.render(pluginCanvas.domElement)
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

                val clientInstance = createClientInstance(instance)

                if (clientInstance.isInstanceOf[EditablePluginInstanceView]) {
                    initializeEditableInstance(clientInstance.asInstanceOf[EditablePluginInstanceView], instance, analysis)
                }

                instancesMap.put(instance.id, clientInstance)

                if (isSource(instance, analysis)) {
                    sources += clientInstance
                }

                renderBuffer += clientInstance
        }
    }

    private def isSource(instance: entities.plugins.PluginInstance, analysis: Analysis): Boolean = {
        !analysis.pluginInstanceBindings.find(_.targetPluginInstance == instance).isDefined
    }

    private def initializeEditableInstance(clientInstance: EditablePluginInstanceView, instance: entities.plugins.PluginInstance,
        analysis: Analysis) {
        clientInstance.hideControls()

        if (instanceHasNoFollowers(analysis, instance)) {
            clientInstance.showControls()
        }

        clientInstance.parameterValueChanged += { e => parameterValueChanged.triggerDirectly(e.target) }
    }

    private def instanceHasNoFollowers(analysis: Analysis, instance: entities.plugins.PluginInstance): Boolean = {
        !analysis.pluginInstanceBindings.find(_.sourcePluginInstance == instance).isDefined
    }

    private def createClientInstance(instance: entities.plugins.PluginInstance) : PluginInstanceView = {
        val defaultValues = getDefaultValues(instance)
        if (editable) {
            new EditablePluginInstanceView(instance.id, instance.plugin, List(), defaultValues)
        } else {
            new ReadOnlyPluginInstanceView(instance.id, instance.plugin, List(), defaultValues)
        }
    }

    private def getDefaultValues(instance: entities.plugins.PluginInstance): mutable.HashMap[String, String] = {
        val map = new mutable.HashMap[String, String]
        instance.parameterValues.foreach {
            v => map.put(v.parameter.name, v.value.toString)
        }
        map
    }

    @javascript(
        """
          jsPlumb.repaintEverything();
          var settings = {
                            paintStyle:{ lineWidth:2, strokeStyle:"#BCE8F1", outlineColor:"#3A87AD", outlineWidth:1 },
                            connector:[ "Flowchart" ],
                            endpoint:[ "Dot", { radius:4 } ],
                            endpointStyle : { fillStyle: "#3A87AD"  },
                            anchor : [ "BottomCenter", "TopCenter" ]
                       };
          jsPlumb.connect({ source:a.getPluginElement(), target:b.getPluginElement() },settings);
        """)
    def renderBinding(a: PluginInstanceView, b: PluginInstanceView) {}
}
